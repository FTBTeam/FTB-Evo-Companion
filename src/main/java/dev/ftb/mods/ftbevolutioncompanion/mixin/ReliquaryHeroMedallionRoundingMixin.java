package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "reliquary.item.util.fluid.FluidHandlerHeroMedallion", remap = false)
public abstract class ReliquaryHeroMedallionRoundingMixin {
    private static final int MB_PER_POINT = 20;

    @ModifyExpressionValue(
            method = {
                    "extract(ILnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
                    "insert(ILnet/neoforged/neoforge/transfer/fluid/FluidResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I"
            },
            at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(II)I"))
    private int ftbevo$wholePoints(int original) {
        return original - original % MB_PER_POINT;
    }
}
