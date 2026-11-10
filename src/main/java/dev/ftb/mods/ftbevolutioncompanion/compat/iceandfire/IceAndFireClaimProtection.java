package dev.ftb.mods.ftbevolutioncompanion.compat.iceandfire;

import dev.ftb.mods.ftbchunks.api.ClaimedChunk;
import dev.ftb.mods.ftbchunks.api.FTBChunksAPI;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.Team;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class IceAndFireClaimProtection {
    private IceAndFireClaimProtection() {}

    public static void register() {
        listen("com.iafenvoy.iceandfire.event.GriefBreakBlockEvent", "getGriefer");
        listen("com.iafenvoy.iceandfire.event.DragonFireDamageWorldEvent", "getDragon");
    }

    @SuppressWarnings("unchecked")
    private static <T extends Event> void listen(String className, String sourceGetter) {
        Class<T> type;
        Method source;
        Method targetX;
        Method targetY;
        Method targetZ;
        try {
            type = (Class<T>) Class.forName(className);
            source = type.getMethod(sourceGetter);
            targetX = type.getMethod("getTargetX");
            targetY = type.getMethod("getTargetY");
            targetZ = type.getMethod("getTargetZ");
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Ice and Fire event " + className + " is missing", e);
        }
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, false, type, event -> {
            try {
                Entity entity = (Entity) source.invoke(event);
                if (entity == null) {
                    return;
                }
                BlockPos pos = BlockPos.containing(
                        (double) targetX.invoke(event), (double) targetY.invoke(event), (double) targetZ.invoke(event));
                if (isProtected(entity, pos)) {
                    ((ICancellableEvent) event).setCanceled(true);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private static boolean isProtected(Entity entity, BlockPos pos) {
        Level level = entity.level();
        if (level.isClientSide() || !FTBChunksAPI.api().isManagerLoaded()) {
            return false;
        }
        ClaimedChunk chunk = FTBChunksAPI.api().getManager().getChunk(new ChunkDimPos(level, pos));
        if (chunk == null) {
            return false;
        }
        return !chunk.getTeamData().allowMobGriefing() && !isOwnedByTeamMember(entity, chunk.getTeamData().getTeam());
    }

    private static boolean isOwnedByTeamMember(Entity entity, Team team) {
        if (!(entity instanceof OwnableEntity ownable)) {
            return false;
        }
        EntityReference<LivingEntity> owner = ownable.getOwnerReference();
        if (owner == null) {
            return false;
        }
        return team.getRankForPlayer(owner.getUUID()).isMemberOrBetter();
    }
}
