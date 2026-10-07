package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "liedge.ltxindustries.integration.jei.LTXIBaseCategory")
public abstract class LTXIJeiFluidSlotMixin {
    @Inject(
            method =
                    "addFluidStack(Lmezz/jei/api/gui/builder/IRecipeSlotBuilder;Lnet/neoforged/neoforge/fluids/FluidStack;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$skipFlowingFluids(IRecipeSlotBuilder slot, FluidStack stack, CallbackInfo ci) {
        Fluid fluid = stack.getFluid();
        if (stack.isEmpty() || !fluid.isSource(fluid.defaultFluidState())) {
            ci.cancel();
        }
    }
}
