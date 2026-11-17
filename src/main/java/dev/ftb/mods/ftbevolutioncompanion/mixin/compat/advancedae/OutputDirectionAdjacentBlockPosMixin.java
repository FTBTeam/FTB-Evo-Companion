package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.advancedae;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.pedroksl.ae2addonlib.gui.OutputDirectionMenu", remap = false)
public abstract class OutputDirectionAdjacentBlockPosMixin {
    @WrapOperation(
            method = "getAdjacentBlock",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/core/BlockPos;relative(Lnet/minecraft/core/Direction;)Lnet/minecraft/core/BlockPos;",
                            ordinal = 1))
    private BlockPos ftbevo$keepAdjacentBlockPos(
            BlockPos adjacentPos, Direction direction, Operation<BlockPos> original) {
        return adjacentPos;
    }
}
