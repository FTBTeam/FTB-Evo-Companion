package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.simplywinged.ParagliderWings;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "in2bubble.simplywinged.In2bubble", remap = false)
public abstract class In2bubbleParagliderMixin {
    @WrapOperation(
            method = {"hasElytraEquipped", "isElytraUsable", "createWingedArmorOutput"},
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean ftbevo$paragliderInsteadOfElytra(ItemStack stack, Object item, Operation<Boolean> original) {
        return ParagliderWings.swapElytraCheck(stack, item, original);
    }

    @WrapOperation(
            method = "damageHorseElytra",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private boolean ftbevo$paragliderWearsDown(ItemStack stack, Object item, Operation<Boolean> original) {
        return ParagliderWings.swapElytraCheck(stack, item, original);
    }
}
