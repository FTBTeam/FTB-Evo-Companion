package dev.ftb.mods.ftbevolutioncompanion.compat.ftbteams;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import java.util.Optional;
import java.util.UUID;

public final class FtbTeamsAllies {
    private FtbTeamsAllies() {}

    public static boolean areAllies(UUID first, UUID second) {
        if (!FTBTeamsAPI.api().isManagerLoaded()) {
            return false;
        }
        TeamManager manager = FTBTeamsAPI.api().getManager();
        if (manager.arePlayersInSameTeam(first, second)) {
            return true;
        }
        return ranksAsAlly(manager.getTeamForPlayerID(first), second)
                || ranksAsAlly(manager.getTeamForPlayerID(second), first);
    }

    private static boolean ranksAsAlly(Optional<Team> team, UUID player) {
        return team.map(t -> t.getRankForPlayer(player).isAllyOrBetter()).orElse(false);
    }
}
