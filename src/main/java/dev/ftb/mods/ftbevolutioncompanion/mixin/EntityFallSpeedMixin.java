package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.FallSpeedTracker;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityFallSpeedMixin implements FallSpeedTracker {
    @Unique
    private double ftbevo$recentFallY;

    @Override
    public double ftbevo$recentFallY() {
        return this.ftbevo$recentFallY;
    }

    @Override
    public void ftbevo$setRecentFallY(double fallY) {
        this.ftbevo$recentFallY = fallY;
    }

    @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"))
    private void ftbevo$trackFallSpeed(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (((Entity) (Object) this).onGround()) {
            this.ftbevo$recentFallY = 0.0;
        } else if (movement.y < this.ftbevo$recentFallY) {
            this.ftbevo$recentFallY = movement.y;
        }
    }
}
