package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.RootsMagic;

import elucent.rootsclassic.item.StaffItem;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = StaffItem.class, remap = false)
public abstract class RootsStaffUsesMixin {
    @ModifyArg(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;"
                    + "Lnet/minecraft/world/entity/LivingEntity;I)Z",
            at = @At(value = "INVOKE", target = "Lelucent/rootsclassic/datacomponent/StaffUses;<init>(II)V"),
            index = 0)
    private int ftbevo$keepStaffUse(int remaining, @Local(argsOnly = true) LivingEntity caster) {
        return RootsMagic.remainingStaffUses(caster, remaining);
    }
}
