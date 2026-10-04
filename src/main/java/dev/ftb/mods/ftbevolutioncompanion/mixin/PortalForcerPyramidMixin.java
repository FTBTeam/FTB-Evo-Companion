package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalForcer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalForcer.class)
public abstract class PortalForcerPyramidMixin {
    private static final int FRAME_REACH = 4;

    @Shadow
    @Final
    private ServerLevel level;

    @Inject(method = "canHostFrame", at = @At("HEAD"), cancellable = true)
    private void ftbevo$keepPortalsOutOfPyramid(
            BlockPos origin,
            BlockPos.MutableBlockPos scratch,
            Direction direction,
            int offset,
            CallbackInfoReturnable<Boolean> cir) {
        if (level.dimension() != Level.OVERWORLD) {
            return;
        }
        if (origin.getX() >= CompanionConfig.PYRAMID_MIN_X.get() - FRAME_REACH
                && origin.getX() <= CompanionConfig.PYRAMID_MAX_X.get() + FRAME_REACH
                && origin.getZ() >= CompanionConfig.PYRAMID_MIN_Z.get() - FRAME_REACH
                && origin.getZ() <= CompanionConfig.PYRAMID_MAX_Z.get() + FRAME_REACH
                && origin.getY() >= CompanionConfig.PYRAMID_MIN_Y.get() - FRAME_REACH - 1
                && origin.getY() <= CompanionConfig.PYRAMID_MAX_Y.get() + 1) {
            cir.setReturnValue(false);
        }
    }
}
