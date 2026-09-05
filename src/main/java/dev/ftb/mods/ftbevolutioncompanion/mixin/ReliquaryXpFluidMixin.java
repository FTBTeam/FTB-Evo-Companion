package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.XpFluidUnifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {
        "reliquary.item.util.fluid.FluidHandlerHeroMedallion",
        "reliquary.item.FortuneCoinItem"
}, remap = false)
public abstract class ReliquaryXpFluidMixin {
    @ModifyExpressionValue(
            method = "*",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;"))
    private static Object ftbevo$unifyXpFluid(Object original) {
        return XpFluidUnifier.substitute(original);
    }
}
