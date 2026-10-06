package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LTXIExtendedInput.class, remap = false)
public abstract class LTXIExtendedInputGunneryMixin {
    @WrapOperation(
            method = "shootWeapon",
            at = @At(
                    value = "INVOKE",
                    target = "Lliedge/ltxindustries/item/weapon/WeaponItem;getFireRate(Lnet/minecraft/world/item/ItemStack;)I"))
    private int ftbevo$fireRate(WeaponItem weapon, ItemStack stack, Operation<Integer> original, @Local(argsOnly = true) Player player) {
        int ticks = original.call(weapon, stack);
        double rate = SkillsHelper.attr(player, GunneryRegistry.GUN_FIRE_RATE);
        return rate > 0.0 && ticks > 0 ? Math.max(1, (int) Math.round(ticks / (1.0 + rate))) : ticks;
    }

    @WrapOperation(
            method = "shootWeapon",
            at = @At(
                    value = "INVOKE",
                    target = "Lliedge/ltxindustries/item/weapon/WeaponItem;weaponFired(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lliedge/ltxindustries/lib/weapons/LTXIExtendedInput;)V"))
    private void ftbevo$extraShot(
            WeaponItem weapon,
            ItemStack stack,
            Player player,
            Level level,
            LTXIExtendedInput input,
            Operation<Void> original) {
        LtxGunnery.fire(player, stack, weapon, () -> original.call(weapon, stack, player, level, input));
        double chance = SkillsHelper.attr(player, GunneryRegistry.GUN_EXTRA_BULLET);
        if (!level.isClientSide() && chance > 0.0 && player.getRandom().nextDouble() < chance) {
            LtxGunnery.fire(player, stack, weapon, () -> original.call(weapon, stack, player, level, input));
        }
    }
}
