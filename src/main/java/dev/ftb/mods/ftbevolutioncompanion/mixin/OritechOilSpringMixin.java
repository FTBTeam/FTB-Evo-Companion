package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "rearth.oritech.init.world.features.oil.OilSpringFeature", remap = false)
public abstract class OritechOilSpringMixin {
    @WrapOperation(
            method = "placeSurfacePatches",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/WorldGenLevel;setBlock("
                            + "Lnet/minecraft/core/BlockPos;"
                            + "Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private static boolean ftbevo$settleSurfaceOil(WorldGenLevel level, BlockPos pos, BlockState state, int flags,
                                                   Operation<Boolean> original) {
        boolean placed = original.call(level, pos, state, flags);
        if (placed) {
            FluidState fluidState = state.getFluidState();
            if (!fluidState.isEmpty()) {
                level.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(level));
            }
        }
        return placed;
    }
}
