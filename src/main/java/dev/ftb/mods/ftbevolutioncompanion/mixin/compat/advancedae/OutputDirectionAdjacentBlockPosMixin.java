package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.advancedae;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.pedroksl.ae2addonlib.gui.OutputDirectionMenu", remap = false)
public abstract class OutputDirectionAdjacentBlockPosMixin {
    @ModifyArg(
            method = "getAdjacentBlock",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/block/state/BlockState;getCloneItemStack(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;ZLnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/item/ItemStack;"),
            index = 0)
    private BlockPos ftbevo$useAdjacentBlockPos(BlockPos pos, @Local BlockPos adjacentPos) {
        return adjacentPos;
    }
}
