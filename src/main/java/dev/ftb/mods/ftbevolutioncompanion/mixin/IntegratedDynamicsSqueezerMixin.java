package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.FallSpeedTracker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.cyclops.integrateddynamics.block.BlockSqueezer", remap = false)
public abstract class IntegratedDynamicsSqueezerMixin {
    @ModifyExpressionValue(
            method = "updateEntityMovementAfterFallOn(Lnet/minecraft/world/level/BlockGetter;"
                    + "Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;getDeltaMovement()"
                            + "Lnet/minecraft/world/phys/Vec3;",
                    ordinal = 0))
    private Vec3 ftbevo$useTrackedFallSpeed(Vec3 original, BlockGetter level, Entity entity) {
        if (entity.level().isClientSide() || !(entity instanceof FallSpeedTracker tracker)) {
            return original;
        }
        double tracked = tracker.ftbevo$recentFallY();
        return tracked < original.y ? new Vec3(original.x, tracked, original.z) : original;
    }
}
