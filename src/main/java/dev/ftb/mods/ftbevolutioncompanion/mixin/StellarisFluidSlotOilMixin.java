package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.StellarisOilUnifier;

import net.minecraft.world.level.material.Fluid;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.exodusstudio.stellaris.common.menus.slot.SpecificFluidContainerSlot", remap = false)
public abstract class StellarisFluidSlotOilMixin {
    @WrapOperation(method = "mayPlace",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/Fluid;isSame(Lnet/minecraft/world/level/material/Fluid;)Z"))
    private boolean ftbevo$acceptOritechOil(Fluid actual, Fluid expected, Operation<Boolean> original) {
        return original.call(actual, expected) || StellarisOilUnifier.isOritechOilFor(actual, expected);
    }
}
