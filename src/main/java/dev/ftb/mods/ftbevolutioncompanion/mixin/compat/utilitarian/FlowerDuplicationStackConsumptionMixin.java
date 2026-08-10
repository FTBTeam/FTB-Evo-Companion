package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.utilitarian;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "cy.jdkdigital.utilitarian.event.EventHandler", remap = false)
public abstract class FlowerDuplicationStackConsumptionMixin {

    @WrapOperation(
            method = "onBoneMeal",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
    )
    private static void ftbevolutioncompanion$keepDurabilityItems(ItemStack stack, int amount, Operation<Void> original) {
        if (stack.has(DataComponents.MAX_DAMAGE)) {
            return;
        }
        original.call(stack, amount);
    }
}
