package dev.ftb.mods.ftbevolutioncompanion.worldgen;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.mixin.RandomSpreadStructurePlacementAccessor;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MeteoriteSpacing {
    private static final Logger LOGGER = LoggerFactory.getLogger(MeteoriteSpacing.class);

    private static final ResourceKey<StructureSet> AE2_METEORITE =
            ResourceKey.create(Registries.STRUCTURE_SET, Identifier.fromNamespaceAndPath("ae2", "meteorite"));

    private MeteoriteSpacing() {
    }

    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        int spacing = CompanionConfig.METEORITE_SPACING.get();
        int separation = CompanionConfig.METEORITE_SEPARATION.get();

        if (separation >= spacing) {
            LOGGER.warn("Meteorite separation ({}) must be less than spacing ({}); leaving AE2 defaults alone",
                    separation, spacing);
            return;
        }

        event.getServer().registryAccess()
                .lookup(Registries.STRUCTURE_SET)
                .flatMap(registry -> registry.getOptional(AE2_METEORITE))
                .ifPresent(set -> {
                    if (set.placement() instanceof RandomSpreadStructurePlacement placement) {
                        RandomSpreadStructurePlacementAccessor accessor =
                                (RandomSpreadStructurePlacementAccessor) placement;
                        accessor.ftbevo$setSpacing(spacing);
                        accessor.ftbevo$setSeparation(separation);
                        LOGGER.info("Set ae2:meteorite structure spacing to {}/{}", spacing, separation);
                    }
                });
    }
}
