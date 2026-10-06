package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import liedge.ltxindustries.item.weapon.AuroraItem;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AuroraItem.class, remap = false)
public abstract class LTXIAuroraGunneryMixin {
    @ModifyArg(
            method = "weaponFired",
            at = @At(
                    value = "INVOKE",
                    target = "Lliedge/ltxindustries/entity/CompoundHitResult;tracePath(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lliedge/ltxindustries/lib/upgrades/Upgrades;DDIDLliedge/ltxindustries/entity/DynamicClipContext$FluidCollisionPredicate;Ljava/util/function/ToDoubleFunction;)Lliedge/ltxindustries/entity/CompoundHitResult;"),
            index = 4)
    private double ftbevo$accuracy(double inaccuracy, @Local(argsOnly = true) Player player) {
        return inaccuracy * Math.max(0.0, 1.0 - SkillsHelper.attr(player, GunneryRegistry.GUN_ACCURACY));
    }

    @ModifyArg(
            method = "weaponFired",
            at = @At(
                    value = "INVOKE",
                    target = "Lliedge/ltxindustries/entity/CompoundHitResult;tracePath(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lliedge/ltxindustries/lib/upgrades/Upgrades;DDIDLliedge/ltxindustries/entity/DynamicClipContext$FluidCollisionPredicate;Ljava/util/function/ToDoubleFunction;)Lliedge/ltxindustries/entity/CompoundHitResult;"),
            index = 5)
    private int ftbevo$piercing(int maxHits, @Local(argsOnly = true) Player player) {
        return maxHits + LtxGunnery.bonusPierce(player);
    }
}
