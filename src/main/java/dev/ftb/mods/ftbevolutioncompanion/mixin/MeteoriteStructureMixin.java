package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "appeng.worldgen.meteorite.MeteoriteStructure", remap = false)
public abstract class MeteoriteStructureMixin {
    @ModifyExpressionValue(
            method = "generatePieces",
            at = @At(value = "NEW", target = "(III)Lnet/minecraft/core/BlockPos;")
    )
    private static BlockPos ftbevo$relocateNetherMeteorite(BlockPos original,
                                                           @Local(ordinal = 0) Holder<Biome> biome,
                                                           @Local(ordinal = 0) WorldgenRandom random) {
        if (!CompanionConfig.NETHER_METEORITE_RELOCATE.get() || !biome.is(BiomeTags.IS_NETHER)) {
            return original;
        }

        int minY = CompanionConfig.netherMinY();
        int maxY = CompanionConfig.netherMaxY();

        return new BlockPos(original.getX(), minY + random.nextInt(maxY - minY + 1), original.getZ());
    }
}
