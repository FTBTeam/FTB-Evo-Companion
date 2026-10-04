package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.HephaestusTools;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.titammods.hephaestus_tools.tools.stat.HarvestTier", remap = false)
public abstract class HephaestusHarvestTierMixin {
    @Inject(
            method = "byOrdinal(I)Lcom/titammods/hephaestus_tools/tools/stat/HarvestTier;",
            at = @At("HEAD"),
            cancellable = true)
    private static void ftbevo$remapTier(int ordinal, CallbackInfoReturnable<Object> cir) {
        if (ordinal == 4) {
            cir.setReturnValue(HephaestusTools.tier(3));
        } else if (ordinal > 5) {
            cir.setReturnValue(HephaestusTools.tier(5));
        }
    }
}
