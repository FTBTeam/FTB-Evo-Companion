package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.industrialforegoing;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.buuz135.industrial.block.transportstorage.transporter.TransporterItemType", remap = false)
public abstract class TransporterItemTypeExtractAmountMixin {

    @WrapOperation(
            method = "update",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/items/IItemHandler;extractItem(IIZ)Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 1
            )
    )
    private ItemStack ftbevolutioncompanion$clampTransferAmount(IItemHandler origin, int slot, int amount, boolean simulate,
                                                                Operation<ItemStack> original,
                                                                @Local(ordinal = 0) ItemStack extracted,
                                                                @Local(ordinal = 1) ItemStack returned) {
        int allowed = amount + returned.getCount();
        int accepted = extracted.getCount() - returned.getCount();
        int transfer = Math.min(allowed, accepted);
        if (transfer <= 0) {
            return ItemStack.EMPTY;
        }
        return original.call(origin, slot, transfer, simulate);
    }
}
