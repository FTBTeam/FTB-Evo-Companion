package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.XpFluidUnifier;

import net.neoforged.neoforge.transfer.fluid.FluidResource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "me.jddev0.ep.block.entity.AbstractPoweredFurnaceBlockEntity", remap = false)
public abstract class EnergizedPowerXpFurnaceMixin {
    @ModifyExpressionValue(
            method = "craftItem",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/transfer/fluid/FluidResource;"
                            + "of(Lnet/minecraft/core/Holder;)"
                            + "Lnet/neoforged/neoforge/transfer/fluid/FluidResource;"))
    private static FluidResource ftbevo$unifyXpFluid(FluidResource original) {
        return XpFluidUnifier.substituteResource(original);
    }
}
