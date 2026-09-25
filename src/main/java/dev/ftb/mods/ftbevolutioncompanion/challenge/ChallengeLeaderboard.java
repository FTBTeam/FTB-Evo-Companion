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

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
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
import java.util.UUID;

public final class ChallengeLeaderboard {
    private static final Logger LOGGER = LoggerFactory.getLogger("ChallengeBoard");
    private static final int RETRY_TICKS = 20;
    private static final String ANNOUNCE = "ftbevolutioncompanion.challenge_board.announce.";

    private static ChallengeSnapshot current = ChallengeSnapshot.EMPTY;
    private static List<Standing> standings = List.of();
    @Nullable
    private static List<UUID> announced = null;
    private static int ticksUntilRefresh = 0;
    private static long ticksUntilAnnounce = 0;
    private static boolean missingChapterLogged = false;

    private record Ranked(UUID teamId, Component name, int percent, int completed, int total, long lastCompletion) {
    }

    private record Standing(UUID teamId, Component name, int percent) {
    }

    private record Built(ChallengeSnapshot snapshot, List<Standing> standings) {
    }

    private ChallengeLeaderboard() {
    }

    public static ChallengeSnapshot current() {
        return current;
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        tickAnnouncements(event.getServer());
        if (--ticksUntilRefresh > 0) return;
        if (!ServerQuestFile.exists()) {
            ticksUntilRefresh = RETRY_TICKS;
            return;
        }
        ticksUntilRefresh = CompanionConfig.CHALLENGE_REFRESH_SECONDS.get() * 20;
        Built built = build(ServerQuestFile.getInstance());
        standings = built.standings();
        if (announced == null && built.snapshot().hasChapter()) {
            announced = teamIds(standings);
            ticksUntilAnnounce = announceIntervalTicks();
        }
        if (CompanionConfig.CHALLENGE_ANNOUNCE_MINUTES.get() == 0) announce(event.getServer(), false);
        if (built.snapshot().equals(current)) return;
        current = built.snapshot();
        PacketDistributor.sendToAllPlayers(new ChallengePayloads.SyncChallengeBoard(current));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new ChallengePayloads.SyncChallengeBoard(current));
        }
    }

    public static void onServerStopped(ServerStoppedEvent event) {
        current = ChallengeSnapshot.EMPTY;
        standings = List.of();
        announced = null;
        ticksUntilRefresh = 0;
        ticksUntilAnnounce = 0;
        missingChapterLogged = false;
    }

    public static void refreshSoon() {
        ticksUntilRefresh = 0;
    }

    private static long announceIntervalTicks() {
        return CompanionConfig.CHALLENGE_ANNOUNCE_MINUTES.get() * 60L * 20L;
    }

    private static void tickAnnouncements(MinecraftServer server) {
        long interval = announceIntervalTicks();
        if (interval <= 0 || announced == null) return;
        if (ticksUntilAnnounce > interval) ticksUntilAnnounce = interval;
        if (--ticksUntilAnnounce > 0) return;
        ticksUntilAnnounce = interval;
        announce(server, !CompanionConfig.CHALLENGE_ANNOUNCE_ONLY_ON_CHANGE.get());
    }

    private static void announce(MinecraftServer server, boolean includeUnchanged) {
        if (announced == null || CompanionConfig.CHALLENGE_ANNOUNCE_MINUTES.get() < 0) return;
        List<Component> lines = new ArrayList<>();
        boolean changed = false;
        for (int i = 0; i < standings.size(); i++) {
            Standing standing = standings.get(i);
            int rank = i + 1;
            int previous = announced.indexOf(standing.teamId()) + 1;
            boolean moved = previous != rank;
            changed |= moved;
            if (moved || includeUnchanged) lines.add(rankLine(rank, standing, previous));
        }
        if (!changed && announced.size() > standings.size()) changed = true;
        announced = teamIds(standings);
        if (lines.isEmpty() || (!changed && !includeUnchanged)) return;
        Component header = Component.translatable(ANNOUNCE + (changed ? "changed" : "standings")).withStyle(ChatFormatting.GOLD);
        server.getPlayerList().broadcastSystemMessage(header, false);
        for (Component line : lines) server.getPlayerList().broadcastSystemMessage(line, false);
    }

    private static Component rankLine(int rank, Standing standing, int previous) {
        if (previous == rank) return Component.translatable(ANNOUNCE + "rank", rank, standing.name(), standing.percent());
        if (previous == 0) return Component.translatable(ANNOUNCE + "rank_new", rank, standing.name(), standing.percent());
        String key = previous > rank ? "rank_up" : "rank_down";
        return Component.translatable(ANNOUNCE + key, rank, standing.name(), standing.percent(), previous);
    }

    private static List<UUID> teamIds(List<Standing> list) {
        return list.stream().map(Standing::teamId).toList();
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

    private static Built build(ServerQuestFile file) {
        Chapter chapter = findChapter(file);
        if (chapter == null) {
            if (!missingChapterLogged) {
                LOGGER.warn("challenge chapter '{}' not found in the quest book; boards stay empty", CompanionConfig.CHALLENGE_CHAPTER.get());
                missingChapterLogged = true;
            }
            return new Built(ChallengeSnapshot.EMPTY, List.of());
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
            ranked.add(new Ranked(team.getId(), team.getColoredName(), percent, completed, total, lastCompletion));
        }

        ranked.sort(Comparator.<Ranked>comparingInt(Ranked::percent).reversed()
                .thenComparingLong(Ranked::lastCompletion)
                .thenComparing(r -> r.name().getString()));

        int size = Math.min(CompanionConfig.CHALLENGE_BOARD_SIZE.get(), ranked.size());
        List<ChallengeSnapshot.Entry> entries = new ArrayList<>(size);
        List<Standing> top = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            Ranked r = ranked.get(i);
            entries.add(new ChallengeSnapshot.Entry(i + 1, r.name(), r.percent(), r.completed(), r.total()));
            top.add(new Standing(r.teamId(), r.name(), r.percent()));
        }
        return new Built(new ChallengeSnapshot(chapter.id, List.copyOf(entries)), List.copyOf(top));
    }
}
