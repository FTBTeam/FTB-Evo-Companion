package dev.ftb.mods.ftbevolutioncompanion.challenge;

import dev.ftb.mods.ftbevolutioncompanion.challenge.network.ChallengePayloads;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public final class ChallengeLeaderboard {
    private static final Logger LOGGER = LoggerFactory.getLogger("ChallengeBoard");
    private static final int RETRY_TICKS = 20;

    private static ChallengeSnapshot current = ChallengeSnapshot.EMPTY;
    private static int ticksUntilRefresh = 0;
    private static boolean missingChapterLogged = false;

    private record Ranked(Component name, int percent, int completed, int total, long lastCompletion) {
    }

    private ChallengeLeaderboard() {
    }

    public static ChallengeSnapshot current() {
        return current;
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        if (--ticksUntilRefresh > 0) return;
        if (!ServerQuestFile.exists()) {
            ticksUntilRefresh = RETRY_TICKS;
            return;
        }
        ticksUntilRefresh = CompanionConfig.CHALLENGE_REFRESH_SECONDS.get() * 20;
        ChallengeSnapshot next = build(ServerQuestFile.getInstance());
        if (next.equals(current)) return;
        current = next;
        PacketDistributor.sendToAllPlayers(new ChallengePayloads.SyncChallengeBoard(current));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ChallengePayloads.SyncChallengeBoard(current));
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        current = ChallengeSnapshot.EMPTY;
        ticksUntilRefresh = 0;
        missingChapterLogged = false;
    }

    public static void refreshSoon() {
        ticksUntilRefresh = 0;
    }

    @Nullable
    public static Chapter findChapter(ServerQuestFile file) {
        String filename = CompanionConfig.CHALLENGE_CHAPTER.get();
        for (Chapter chapter : file.getAllChapters()) {
            if (filenameOf(chapter).equals(filename)) return chapter;
        }
        return null;
    }

    private static String filenameOf(Chapter chapter) {
        String name = chapter.getPath().map(path -> path.getFileName().toString()).orElse("");
        int dot = name.lastIndexOf('.');
        return dot > 0 ? name.substring(0, dot) : name;
    }

    private static ChallengeSnapshot build(ServerQuestFile file) {
        Chapter chapter = findChapter(file);
        if (chapter == null) {
            if (!missingChapterLogged) {
                LOGGER.warn("challenge chapter '{}' not found in the quest book; boards stay empty", CompanionConfig.CHALLENGE_CHAPTER.get());
                missingChapterLogged = true;
            }
            return ChallengeSnapshot.EMPTY;
        }
        missingChapterLogged = false;

        TeamManager teams = FTBTeamsAPI.api().getManager();
        List<Ranked> ranked = new ArrayList<>();
        for (TeamData data : file.getAllTeamData()) {
            Team team = teams.getTeamByID(data.getTeamId()).orElse(null);
            if (team == null) continue;
            int percent = data.getRelativeProgress(chapter);
            if (percent <= 0) continue;
            int completed = 0;
            int total = 0;
            long lastCompletion = Long.MAX_VALUE;
            for (Quest quest : chapter.getQuests()) {
                if (quest.isOptionalForProgression(data)) continue;
                total++;
                if (!data.isCompleted(quest)) continue;
                completed++;
                long when = data.getCompletedTime(quest.id).map(Date::getTime).orElse(0L);
                lastCompletion = lastCompletion == Long.MAX_VALUE ? when : Math.max(lastCompletion, when);
            }
            ranked.add(new Ranked(team.getColoredName(), percent, completed, total, lastCompletion));
        }

        ranked.sort(Comparator.<Ranked>comparingInt(Ranked::percent).reversed()
                .thenComparingLong(Ranked::lastCompletion)
                .thenComparing(r -> r.name().getString()));

        int size = Math.min(CompanionConfig.CHALLENGE_BOARD_SIZE.get(), ranked.size());
        List<ChallengeSnapshot.Entry> entries = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Ranked r = ranked.get(i);
            entries.add(new ChallengeSnapshot.Entry(i + 1, r.name(), r.percent(), r.completed(), r.total()));
        }
        return new ChallengeSnapshot(chapter.id, List.copyOf(entries));
    }
}
