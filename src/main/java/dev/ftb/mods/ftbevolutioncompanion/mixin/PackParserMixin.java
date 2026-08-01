package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.mts.MtsItemRemovals;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "minecrafttransportsimulator.packloading.PackParser", remap = false)
public abstract class PackParserMixin {
    @Inject(method = "registerItem", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$dropRemovedPackItems(@Coerce Object definition, CallbackInfo ci) {
        if (MtsItemRemovals.shouldDropDefinition(definition)) {
            ci.cancel();
        }
    }
}
