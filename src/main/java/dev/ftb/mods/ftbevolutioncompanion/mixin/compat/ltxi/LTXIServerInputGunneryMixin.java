package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ServerExtendedInput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ServerExtendedInput.class, remap = false)
public abstract class LTXIServerInputGunneryMixin {
    @WrapOperation(
            method = "startReload",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lliedge/ltxindustries/item/weapon/WeaponItem;getReloadSpeed(Lnet/minecraft/world/item/ItemStack;)I"))
    private int ftbevo$reloadSpeed(
            WeaponItem weapon, ItemStack stack, Operation<Integer> original, @Local(argsOnly = true) Player player) {
        int ticks = original.call(weapon, stack);
        double speed = SkillsHelper.attr(player, GunneryRegistry.GUN_RELOAD_SPEED);
        return speed > 0.0 && ticks > 0 ? Math.max(1, (int) Math.round(ticks / (1.0 + speed))) : ticks;
    }

    @WrapOperation(
            method = "shootWeapon",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lliedge/ltxindustries/item/weapon/WeaponItem;setAmmoLoaded(Lnet/minecraft/world/item/ItemStack;I)V"))
    private void ftbevo$ammoSave(
            WeaponItem weapon,
            ItemStack stack,
            int ammo,
            Operation<Void> original,
            @Local(argsOnly = true) Player player) {
        double save = SkillsHelper.attr(player, GunneryRegistry.GUN_AMMO_SAVE);
        if (LtxGunnery.hasInfiniteAmmo(stack)
                || (save > 0.0 && player.getRandom().nextDouble() < save)) {
            return;
        }
        original.call(weapon, stack, ammo);
    }
}
