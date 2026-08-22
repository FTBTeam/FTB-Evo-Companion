package dev.ftb.mods.ftbevolutioncompanion.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.api.magic.Affinity", remap = false)
public abstract class ArsMagicaAffinityToStringMixin {
    @Shadow
    @Final
    private int color;

    @Shadow
    @Final
    private double index;

    @Inject(method = "toString", at = @At("HEAD"), cancellable = true)
    private void ftbevo$nonRecursiveToString(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue("Affinity[color=" + color + ", index=" + index + "]");
    }
}
