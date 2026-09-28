package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.artifice;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.gunnery.GunneryRegistry;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import io.redspace.irons_artifice.item.CowboyHatItem;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = CowboyHatItem.class, remap = false)
public abstract class CowboyHatQuickdrawMixin {
    @ModifyArg(
            method = "performInstantReload",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/ItemCooldowns;addCooldown(Lnet/minecraft/world/item/ItemStack;I)V"),
            index = 1)
    private static int ftbevo$quickdraw(int ticks, @Local(argsOnly = true) LivingEntity attacker) {
        double reduction = SkillsHelper.attr(attacker, GunneryRegistry.GUN_QUICKDRAW);
        return reduction > 0.0 ? Math.max(1, (int) Math.round(ticks * (1.0 - reduction))) : ticks;
    }
}
