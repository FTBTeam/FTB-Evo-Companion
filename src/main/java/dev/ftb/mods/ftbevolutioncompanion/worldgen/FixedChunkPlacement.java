package dev.ftb.mods.ftbevolutioncompanion.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class FixedChunkPlacement extends StructurePlacement {
    public static final DeferredRegister<StructurePlacementType<?>> PLACEMENT_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, FTBEvolutionCompanion.MOD_ID);

    public static final MapCodec<FixedChunkPlacement> CODEC = RecordCodecBuilder.mapCodec(instance ->
            placementCodec(instance).and(instance.group(
                    Codec.INT.fieldOf("chunk_x").forGetter(p -> p.chunkX),
                    Codec.INT.fieldOf("chunk_z").forGetter(p -> p.chunkZ)
            )).apply(instance, FixedChunkPlacement::new));

    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<FixedChunkPlacement>> FIXED_CHUNK =
            PLACEMENT_TYPES.register("fixed_chunk", () -> () -> CODEC);

    private final int chunkX;
    private final int chunkZ;

    public FixedChunkPlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency,
                               int salt, Optional<ExclusionZone> exclusionZone, int chunkX, int chunkZ) {
        super(locateOffset, frequencyReductionMethod, frequency, salt, exclusionZone);
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @Override
    protected boolean isPlacementChunk(ChunkGeneratorStructureState state, int x, int z) {
        return x == chunkX && z == chunkZ;
    }

    @Override
    public StructurePlacementType<?> type() {
        return FIXED_CHUNK.get();
    }
}
