package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.productivefarming;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "cy.jdkdigital.productivefarming.common.block.ProductiveCropBlock", remap = false)
public abstract class ProductiveCropYieldMaturityMixin {
    @WrapWithCondition(
            method = "lambda$getDrops$0",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;grow(I)V"))
    private static boolean ftbevo$yieldOnlyWhenMature(
            ItemStack stack, int amount, @Local(argsOnly = true) BlockState state) {
        return state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state);
    }
}
