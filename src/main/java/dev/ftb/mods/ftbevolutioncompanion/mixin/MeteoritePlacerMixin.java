package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.world.level.LevelAccessor;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "appeng.worldgen.meteorite.MeteoritePlacer", remap = false)
public abstract class MeteoritePlacerMixin {
    @Shadow
    @Final
    private LevelAccessor level;

    @Inject(method = "placeCrater", at = @At("HEAD"), cancellable = true)
    private void ftbevo$skipCrater(CallbackInfo ci) {
        if (ftbevo$shouldSuppressCrater()) {
            ci.cancel();
        }
    }

    @Inject(method = "decay", at = @At("HEAD"), cancellable = true)
    private void ftbevo$skipDecay(CallbackInfo ci) {
        if (ftbevo$shouldSuppressCrater()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean ftbevo$shouldSuppressCrater() {
        return CompanionConfig.NETHER_METEORITE_SUPPRESS_CRATER.get() && level.dimensionType().hasCeiling();
    }
}
