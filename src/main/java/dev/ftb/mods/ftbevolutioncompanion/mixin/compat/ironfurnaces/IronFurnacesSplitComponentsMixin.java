package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ironfurnaces;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "ironfurnaces.tileentity.furnaces.BlockIronFurnaceTileBase", remap = false)
public abstract class IronFurnacesSplitComponentsMixin {
    @WrapOperation(
            method = "split",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;",
                            ordinal = 0))
    private Item ftbevo$matchComponents(
            ItemStack stack, Operation<Item> original, @Local(ordinal = 0) ItemStack itemToCheck) {
        return ItemStack.isSameItemSameComponents(stack, itemToCheck) ? original.call(stack) : null;
    }
}
