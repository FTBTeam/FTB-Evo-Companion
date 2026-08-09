package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.phys.Vec3;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BowItem.class, CrossbowItem.class})
public abstract class MultishotPatternMixin {
    private static final double DIAGONAL = Math.sqrt(0.5);

    @Inject(
            method = "shootProjectile(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/projectile/Projectile;IFFFLnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("RETURN"))
    private void ftbevo$xPattern(LivingEntity shooter, Projectile projectile, int index, float velocity,
                                 float inaccuracy, float angle, LivingEntity target, CallbackInfo ci) {
        if (angle == 0.0F || target != null
                || !(shooter instanceof Player player)
                || SkillsHelper.attr(player, SkillsRegistry.MULTISHOT) <= 0.0
                || !CompanionConfig.MULTISHOT_X_PATTERN.get()) {
            return;
        }

        Vec3 up = shooter.getUpVector(1.0F);
        Vec3 right = shooter.getViewVector(1.0F).cross(up);
        if (up.lengthSqr() < 1.0E-6 || right.lengthSqr() < 1.0E-6) {
            return;
        }
        right = right.normalize();

        double yawCorrection = angle * (DIAGONAL - 1.0);
        double pitchOffset = angle * DIAGONAL * (((index + 1) / 2) % 2 == 1 ? 1.0 : -1.0);

        Vector3f direction = projectile.getDeltaMovement().toVector3f();
        direction.rotateAxis((float) Math.toRadians(yawCorrection), (float) up.x, (float) up.y, (float) up.z);
        direction.rotateAxis((float) Math.toRadians(pitchOffset), (float) right.x, (float) right.y, (float) right.z);
        projectile.setDeltaMovement(new Vec3(direction));
    }
}
