package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.oceanmobs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.compat.oceanmobs.RiftArena;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.ftb.mods.ftboceanmobs.entity.riftweaver.SeismicSmasher", remap = false)
public abstract class SeismicSmasherMixin {
    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"))
    private BlockPos ftbevo$arenaHeight(
            Level level, Heightmap.Types type, BlockPos pos, Operation<BlockPos> original) {
        return RiftArena.heightmapPos(level, type, pos, original.call(level, type, pos));
    }
}
