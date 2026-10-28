package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.reliquary;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

@Pseudo
@Mixin(targets = "reliquary.item.component.OversizedComponentItemHandler", remap = false)
public abstract class OversizedHandlerEmptyStackMixin {
    @ModifyExpressionValue(
            method = "setStackInSlot(ILnet/minecraft/world/item/ItemStack;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ljava/util/function/BiPredicate;test(Ljava/lang/Object;Ljava/lang/Object;)Z"))
    private boolean ftbevo$emptyStackIsValid(boolean original, int slot, ItemStack stack) {
        return original || stack.isEmpty();
    }
}
