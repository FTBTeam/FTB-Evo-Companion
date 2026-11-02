package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.witchery;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.sterner.witchery.content.block.oven.OvenBlockEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = OvenBlockEntity.class, remap = false)
public abstract class OvenFuelRemainderMixin {
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
    private void ftbevo$rememberFuel(
            ItemStack fuel, int amount, Operation<Void> original, @Share("fuel") LocalRef<ItemStack> fuelCopy) {
        fuelCopy.set(fuel.copy());
        original.call(fuel, amount);
    }

    @WrapOperation(
            method = "tick",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/Item;getCraftingRemainder()Lnet/minecraft/world/item/ItemStackTemplate;"))
    private ItemStackTemplate ftbevo$stackRemainder(
            Item item, Operation<ItemStackTemplate> original, @Share("fuel") LocalRef<ItemStack> fuelCopy) {
        ItemStack fuel = fuelCopy.get();
        if (fuel == null || fuel.isEmpty()) {
            return original.call(item);
        }
        return item.getCraftingRemainder(fuel);
    }
}
