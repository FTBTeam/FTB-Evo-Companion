package dev.ftb.mods.ftbevolutioncompanion.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;

public class SpawnPyramidStructure extends Structure {
    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, FTBEvolutionCompanion.MOD_ID);

    public static final MapCodec<SpawnPyramidStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                    Codec.INT.fieldOf("start_height").forGetter(s -> s.startHeight)
            ).apply(instance, SpawnPyramidStructure::new));

    public static final DeferredHolder<StructureType<?>, StructureType<SpawnPyramidStructure>> TYPE =
            STRUCTURE_TYPES.register("spawn_pyramid", () -> () -> CODEC);

    private final Holder<StructureTemplatePool> startPool;
    private final int startHeight;

    public SpawnPyramidStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int startHeight) {
        super(settings);
        this.startPool = startPool;
        this.startHeight = startHeight;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        BlockPos pos = new BlockPos(context.chunkPos().getMinBlockX(), startHeight, context.chunkPos().getMinBlockZ());
        return JigsawPlacement.addPieces(context, startPool, Optional.empty(), 7, pos, false, Optional.empty(),
                new JigsawStructure.MaxDistance(128, 256), PoolAliasLookup.EMPTY,
                JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
    }

    @Override
    public StructureType<?> type() {
        return TYPE.get();
    }
}
