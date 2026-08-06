package dev.ftb.mods.ftbevolutioncompanion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * GuideME 26.1.12-beta converts a mouse position into the guide's virtual
 * (independently scaled) coordinate space twice before hit-testing widgets, so
 * clicks never reach them.
 *
 * Remove once GuideME ships a fix:
 * https://github.com/AppliedEnergistics/GuideME/issues/105
 */
@Mixin(targets = "guideme.internal.screen.IndepentScaleScreen", remap = false)
public abstract class IndepentScaleScreenMixin {

    @Redirect(
            method = "getChildAt(DD)Ljava/util/Optional;",
            at = @At(value = "INVOKE", target = "Lguideme/internal/screen/IndepentScaleScreen;toVirtual(D)D"),
            require = 2)
    private double ftbevo$keepAlreadyVirtualCoordinate(@Coerce Object screen, double value) {
        return value;
    }
}
