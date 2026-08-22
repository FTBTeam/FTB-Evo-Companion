package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFluidTravelMixin {
    @Shadow
    protected abstract double getEffectiveGravity();

    @Shadow
    protected abstract void travelInWater(Vec3 input, double baseGravity, boolean isFalling, double oldY);

    @Inject(method = "travelInFluid", at = @At("HEAD"), cancellable = true)
    private void ftbevo$moddedFluidTravelFallback(Vec3 input, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isInWater() || self.isInLava()) {
            return;
        }
        boolean isFalling = self.getDeltaMovement().y <= 0.0;
        double oldY = self.getY();
        double gravity = this.getEffectiveGravity();
        boolean handled = self.getFluidInteraction().isInFluidMatching(self,
                (entity, type, height) -> !type.isVanilla() && entity.moveInFluid(type, input, gravity));
        if (!handled) {
            this.travelInWater(input, gravity, isFalling, oldY);
        }
        ci.cancel();
    }
}
