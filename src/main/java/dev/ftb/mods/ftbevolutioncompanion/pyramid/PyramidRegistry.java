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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkylineBlockEntity>> SKYLINE =
            BLOCK_ENTITIES.register("ftb_skyline",
                    () -> new BlockEntityType<>(SkylineBlockEntity::new, Set.of(CompanionContent.SKYLINE.get())));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SkylinePartBlockEntity>> SKYLINE_PART =
            BLOCK_ENTITIES.register("ftb_skyline_part",
                    () -> new BlockEntityType<>(SkylinePartBlockEntity::new, Set.of(CompanionContent.SKYLINE_PART.get())));

    static {
        BLOCK_ENTITIES.addAlias(FTBEvolutionCompanion.id("evolution_pyramid"), SKYLINE.getId());
        BLOCK_ENTITIES.addAlias(FTBEvolutionCompanion.id("evolution_pyramid_part"), SKYLINE_PART.getId());
    }

    private PyramidRegistry() {
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, SKYLINE_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.ITEM ? part.core().map(core -> core.handlers().items()).orElse(null) : null);
        event.registerBlockEntity(Capabilities.Fluid.BLOCK, SKYLINE_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.FLUID ? part.core().map(core -> core.handlers().fluids()).orElse(null) : null);
        event.registerBlockEntity(Capabilities.Energy.BLOCK, SKYLINE_PART.get(), (part, side) ->
                part.bay() == PyramidLayout.Bay.ENERGY ? part.core().map(core -> core.handlers().energy()).orElse(null) : null);
    }
}
