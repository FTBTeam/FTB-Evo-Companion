package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs.RiftArena;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrialSpawner.class)
public abstract class TrialSpawnerMixin {
    @Inject(method = "shouldMobBeUntracked", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$keepTrackingRiftWeaver(
            ServerLevel level, BlockPos spawnerPos, UUID id, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = level.getEntity(id);
        if (entity != null
                && entity.isAlive()
                && entity.level() == level
                && RiftArena.isRiftWeaver(entity.getType())
                && entity.blockPosition().distSqr(spawnerPos)
                        <= (double) RiftArena.TRACKING_RANGE * RiftArena.TRACKING_RANGE) {
            cir.setReturnValue(false);
        }
    }
}
