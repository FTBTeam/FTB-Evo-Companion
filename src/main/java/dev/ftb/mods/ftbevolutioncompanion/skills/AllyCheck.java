package dev.ftb.mods.ftbevolutioncompanion.skills;

import dev.ftb.mods.ftbevolutioncompanion.compat.ftbteams.FtbTeamsAllies;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import net.neoforged.fml.ModList;

import java.util.UUID;

public final class AllyCheck {
    private static Boolean ftbTeamsLoaded;

    private AllyCheck() {
    }

    public static boolean isHostileTarget(ServerPlayer player, LivingEntity entity) {
        if (entity == player || !entity.isAlive() || entity.isSpectator()) {
            return false;
        }
        if (entity instanceof Player other) {
            return !other.isCreative() && player.canHarmPlayer(other) && !areAllies(player, other);
        }
        if (player.isAlliedTo(entity)) {
            return false;
        }
        if (entity instanceof OwnableEntity ownable) {
            EntityReference<LivingEntity> owner = ownable.getOwnerReference();
            if (owner != null && isAlly(player, owner.getUUID(), ownable.getOwner())) {
                return false;
            }
        }
        return entity instanceof Enemy || (entity instanceof Mob mob && mob.getTarget() == player);
    }

    public static boolean areAllies(ServerPlayer player, Player other) {
        return isAlly(player, other.getUUID(), other);
    }

    private static boolean isAlly(ServerPlayer player, UUID otherId, LivingEntity other) {
        if (player.getUUID().equals(otherId)) {
            return true;
        }
        if (other != null && player.isAlliedTo(other)) {
            return true;
        }
        return ftbTeamsLoaded() && FtbTeamsAllies.areAllies(player.getUUID(), otherId);
    }

    private static boolean ftbTeamsLoaded() {
        if (ftbTeamsLoaded == null) {
            ftbTeamsLoaded = ModList.get().isLoaded("ftbteams");
        }
        return ftbTeamsLoaded;
    }
}
