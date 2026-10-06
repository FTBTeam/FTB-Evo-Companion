package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = WeaponItem.class, remap = false)
public abstract class LTXIWeaponGunneryMixin {
    @WrapOperation(
            method = "hurtEntity",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/Entity;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean ftbevo$gunneryDamage(
            Entity target, ServerLevel level, DamageSource source, float amount, Operation<Boolean> original) {
        if (!(source.getEntity() instanceof LivingEntity shooter)) {
            return original.call(target, level, source, amount);
        }
        boolean hit = original.call(target, level, source, LtxGunnery.modifyDamage(shooter, source, amount));
        if (hit) {
            LtxGunnery.onHit(shooter, target, source);
        }
        double knockback = SkillsHelper.attr(shooter, GunneryRegistry.GUN_KNOCKBACK);
        if (hit && knockback > 0.0 && target instanceof LivingEntity living && living != shooter) {
            living.knockback(knockback, shooter.getX() - living.getX(), shooter.getZ() - living.getZ());
        }
        return hit;
    }

    @ModifyArg(
            method = "traceLightfrag",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lliedge/ltxindustries/entity/CompoundHitResult;tracePath(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lliedge/ltxindustries/lib/upgrades/Upgrades;DDIDLliedge/ltxindustries/entity/DynamicClipContext$FluidCollisionPredicate;D)Lliedge/ltxindustries/entity/CompoundHitResult;"),
            index = 4)
    private double ftbevo$accuracy(double inaccuracy, @Local(argsOnly = true) Player player) {
        return inaccuracy * Math.max(0.0, 1.0 - SkillsHelper.attr(player, GunneryRegistry.GUN_ACCURACY));
    }

    @ModifyArg(
            method = "traceLightfrag",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lliedge/ltxindustries/entity/CompoundHitResult;tracePath(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lliedge/ltxindustries/lib/upgrades/Upgrades;DDIDLliedge/ltxindustries/entity/DynamicClipContext$FluidCollisionPredicate;D)Lliedge/ltxindustries/entity/CompoundHitResult;"),
            index = 5)
    private int ftbevo$piercing(int maxHits, @Local(argsOnly = true) Player player) {
        return maxHits + LtxGunnery.bonusPierce(player);
    }
}
