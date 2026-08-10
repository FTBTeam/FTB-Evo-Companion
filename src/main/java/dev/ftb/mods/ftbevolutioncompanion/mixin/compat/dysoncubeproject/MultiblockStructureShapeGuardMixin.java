package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dysoncubeproject;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.buuz135.dysoncubeproject.block.MultiblockStructureBlock", remap = false)
public abstract class MultiblockStructureShapeGuardMixin {

    private static final int FTBEVOLUTIONCOMPANION$MAX_CONTROLLER_DISTANCE = 8;

    @Inject(method = "getSplitShapeRelativeToController", at = @At("HEAD"), cancellable = true)
    private static void ftbevolutioncompanion$skipForeignController(BlockGetter level, BlockPos controllerPos, BlockPos currentPos,
                                                                   CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (level == null || controllerPos == null || currentPos == null) {
            return;
        }
        if (Math.abs(controllerPos.getX() - currentPos.getX()) > FTBEVOLUTIONCOMPANION$MAX_CONTROLLER_DISTANCE
                || Math.abs(controllerPos.getY() - currentPos.getY()) > FTBEVOLUTIONCOMPANION$MAX_CONTROLLER_DISTANCE
                || Math.abs(controllerPos.getZ() - currentPos.getZ()) > FTBEVOLUTIONCOMPANION$MAX_CONTROLLER_DISTANCE) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
