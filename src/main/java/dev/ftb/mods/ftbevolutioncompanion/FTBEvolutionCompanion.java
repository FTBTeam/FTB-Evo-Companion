package dev.ftb.mods.ftbevolutioncompanion;

import net.minecraft.resources.Identifier;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FTBEvolutionCompanion.MOD_ID)
public class FTBEvolutionCompanion {
    public static final String MOD_ID = "ftbevolutioncompanion";

    private static final Logger LOGGER = LoggerFactory.getLogger(FTBEvolutionCompanion.class);

    public FTBEvolutionCompanion(IEventBus eventBus, ModContainer container, Dist dist) {
        if (dist == Dist.CLIENT) {
            eventBus.<FMLClientSetupEvent>addListener(event -> clientSetup(event, eventBus));
        }
    }

    private void clientSetup(FMLClientSetupEvent event, IEventBus eventBus) {
        // Client init
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
