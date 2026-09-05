package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.XpFluidUnifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.misterd.mobflowutilities.block.custom.CollectorBlock", remap = false)
public abstract class MobFlowCollectorXpBucketMixin {
    @ModifyExpressionValue(
            method = "*",
            at = @At(value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/registries/DeferredHolder;get()Ljava/lang/Object;"))
    private static Object ftbevo$unifyXpBucket(Object original) {
        return XpFluidUnifier.substitute(original);
    }
}
