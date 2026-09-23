package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeLeaderboard;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.EnergyTask;
import dev.ftb.mods.ftbquests.quest.task.FluidTask;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;

import net.minecraft.server.level.ServerPlayer;

import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public final class PyramidQuests {
    private PyramidQuests() {
    }

    @Nullable
    public static Chapter chapter() {
        return ServerQuestFile.exists() ? ChallengeLeaderboard.findChapter(ServerQuestFile.getInstance()) : null;
    }

    public static Optional<Team> ownerTeam(@Nullable UUID owner) {
        if (owner == null || !FTBTeamsAPI.api().isManagerLoaded()) return Optional.empty();
        return FTBTeamsAPI.api().getManager().getTeamForPlayerID(owner);
    }

    @Nullable
    public static TeamData teamData(@Nullable UUID owner) {
        if (!ServerQuestFile.exists()) return null;
        return ownerTeam(owner).map(team -> ServerQuestFile.getInstance().getOrCreateTeamData(team)).orElse(null);
    }

    public static boolean isMember(ServerPlayer player, @Nullable UUID owner) {
        Optional<Team> ownerTeam = ownerTeam(owner);
        if (ownerTeam.isEmpty()) return false;
        return FTBTeamsAPI.api().getManager().getTeamForPlayer(player)
                .map(team -> team.getId().equals(ownerTeam.get().getId()))
                .orElse(false);
    }

    public static boolean inChapter(Quest quest, @Nullable Chapter chapter) {
        return chapter != null && quest.getChapter() == chapter;
    }

    public static boolean isAvailable(TeamData data, Quest quest) {
        return data.canStartTasks(quest) && !data.isCompleted(quest);
    }

    public static boolean isDeliverable(Task task) {
        return (task instanceof ItemTask || task instanceof FluidTask || task instanceof EnergyTask) && task.consumesResources();
    }

    public static boolean canDeliver(TeamData data, Task task) {
        return isDeliverable(task) && !data.isLocked() && isAvailable(data, task.getQuest()) && !data.isCompleted(task);
    }

    @Nullable
    public static LaunchTask launchTask(Quest quest) {
        for (Task task : quest.getTasksAsList()) {
            if (task instanceof LaunchTask launch) return launch;
        }
        return null;
    }

    public static boolean isReadyToLaunch(TeamData data, Quest quest) {
        LaunchTask launch = launchTask(quest);
        if (launch == null || data.isCompleted(launch) || !isAvailable(data, quest)) return false;
        for (Task task : quest.getTasksAsList()) {
            if (task instanceof LaunchTask) continue;
            if (!data.isCompleted(task) && !task.isOptionalForProgression(data)) return false;
        }
        return true;
    }

    @Nullable
    public static Task nextTask(TeamData data, Quest quest) {
        if (!isAvailable(data, quest)) return null;
        for (Task task : quest.getTasksAsList()) {
            if (canDeliver(data, task)) return task;
        }
        LaunchTask launch = launchTask(quest);
        return launch != null && !data.isCompleted(launch) ? launch : null;
    }
}
