package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public final class PyramidRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EvolutionPyramidBlockEntity>> EVOLUTION_PYRAMID =
            BLOCK_ENTITIES.register("evolution_pyramid",
                    () -> new BlockEntityType<>(EvolutionPyramidBlockEntity::new, Set.of(CompanionContent.EVOLUTION_PYRAMID.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EvolutionPyramidPartBlockEntity>> EVOLUTION_PYRAMID_PART =
            BLOCK_ENTITIES.register("evolution_pyramid_part",
                    () -> new BlockEntityType<>(EvolutionPyramidPartBlockEntity::new, Set.of(CompanionContent.EVOLUTION_PYRAMID_PART.get())));

    private PyramidRegistry() {
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, EVOLUTION_PYRAMID_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.ITEM ? part.core().map(core -> core.handlers().items()).orElse(null) : null);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, EVOLUTION_PYRAMID_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.FLUID ? part.core().map(core -> core.handlers().fluids()).orElse(null) : null);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, EVOLUTION_PYRAMID_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.ENERGY ? part.core().map(core -> core.handlers().energy()).orElse(null) : null);
    }
}
