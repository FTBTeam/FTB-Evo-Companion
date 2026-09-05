package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "com.leclowndu93150.craftingstationjei.menu.CraftingStationMenu$SideContainerSlot", remap = false)
public abstract class CraftingStationSideSlotMixin {
    @ModifyVariable(
            method = "remove(I)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0)
    private int ftbevo$clampToStackSize(int amount) {
        ItemStack stack = ((Slot) (Object) this).getItem();
        return stack.isEmpty() ? amount : Math.min(amount, stack.getMaxStackSize());
    }
}
