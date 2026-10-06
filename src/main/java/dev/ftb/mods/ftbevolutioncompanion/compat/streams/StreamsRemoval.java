package dev.ftb.mods.ftbevolutioncompanion.compat.streams;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class StreamsRemoval {
    private static final String STREAMS = "streamsreflowing";
    private static final Identifier WATER = Identifier.withDefaultNamespace("water");

    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, FTBEvolutionCompanion.MOD_ID);
    private static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, FTBEvolutionCompanion.MOD_ID);

    private StreamsRemoval() {}

    public static void register(IEventBus eventBus) {
        if (ModList.get().isLoaded(STREAMS)) {
            return;
        }
        BLOCKS.addAlias(Identifier.fromNamespaceAndPath(STREAMS, "stream"), WATER);
        FLUIDS.addAlias(Identifier.fromNamespaceAndPath(STREAMS, "stream"), WATER);
        FLUIDS.addAlias(Identifier.fromNamespaceAndPath(STREAMS, "flowing_stream"), WATER);
        BLOCKS.register(eventBus);
        FLUIDS.register(eventBus);
    }
}
