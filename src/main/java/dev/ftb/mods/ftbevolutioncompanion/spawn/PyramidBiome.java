package dev.ftb.mods.ftbevolutioncompanion.spawn;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;

import net.neoforged.neoforge.event.level.LevelEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PyramidBiome {
    private static final Logger LOGGER = LoggerFactory.getLogger("PyramidBiome");
    public static final ResourceKey<Biome> KEY = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("ftb", "pyramid"));

    private static volatile BiomeSource overworldSource;
    private static volatile Holder<Biome> holder;
    private static int minX, maxX, minY, maxY, minZ, maxZ;

    private PyramidBiome() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;
        if (!CompanionConfig.PYRAMID_BIOME.get()) {
            overworldSource = null;
            return;
        }
        var registry = level.registryAccess().lookupOrThrow(Registries.BIOME);
        var found = registry.get(KEY);
        if (found.isEmpty()) {
            LOGGER.warn("biome {} is not registered, the spawn pyramid keeps the natural biome", KEY.identifier());
            overworldSource = null;
            return;
        }
        holder = found.get();
        minX = QuartPos.fromBlock(CompanionConfig.PYRAMID_MIN_X.get());
        maxX = QuartPos.fromBlock(CompanionConfig.PYRAMID_MAX_X.get());
        minZ = QuartPos.fromBlock(CompanionConfig.PYRAMID_MIN_Z.get());
        maxZ = QuartPos.fromBlock(CompanionConfig.PYRAMID_MAX_Z.get());
        minY = QuartPos.fromBlock(CompanionConfig.PYRAMID_MIN_Y.get());
        maxY = QuartPos.fromBlock(CompanionConfig.PYRAMID_MAX_Y.get());
        overworldSource = level.getChunkSource().getGenerator().getBiomeSource();
        LOGGER.info("spawn pyramid biome active over blocks x {}..{} y {}..{} z {}..{}",
                CompanionConfig.PYRAMID_MIN_X.get(), CompanionConfig.PYRAMID_MAX_X.get(),
                CompanionConfig.PYRAMID_MIN_Y.get(), CompanionConfig.PYRAMID_MAX_Y.get(),
                CompanionConfig.PYRAMID_MIN_Z.get(), CompanionConfig.PYRAMID_MAX_Z.get());
    }

    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level && level.dimension() == Level.OVERWORLD) {
            overworldSource = null;
            holder = null;
        }
    }

    public static Holder<Biome> override(BiomeSource source, int quartX, int quartY, int quartZ) {
        if (source != overworldSource) return null;
        if (quartX < minX || quartX > maxX || quartZ < minZ || quartZ > maxZ || quartY < minY || quartY > maxY) return null;
        return holder;
    }
}
