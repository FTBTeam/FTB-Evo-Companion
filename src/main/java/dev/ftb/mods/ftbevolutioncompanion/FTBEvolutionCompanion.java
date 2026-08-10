package dev.ftb.mods.ftbevolutioncompanion;

import dev.ftb.mods.ftbevolutioncompanion.client.ClientBootstrap;
import dev.ftb.mods.ftbevolutioncompanion.compat.arsnouveau.ShipSpellGuard;
import dev.ftb.mods.ftbevolutioncompanion.network.ModPayloads;
import dev.ftb.mods.ftbevolutioncompanion.ship.ShipSavedTeleportHooks;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(FTBEvolutionCompanion.MOD_ID)
public class FTBEvolutionCompanion {
    public static final String MOD_ID = "ftbevolutioncompanion";

    public FTBEvolutionCompanion(IEventBus eventBus, ModContainer container) {
        eventBus.addListener(FTBEvolutionCompanion::onCommonSetup);
        eventBus.addListener(ModPayloads::register);

        if (ModList.get().isLoaded("ars_nouveau")) {
            ShipSpellGuard.register();
        }

        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientBootstrap.init(eventBus);
        }
    }

    private static void onCommonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("ftbessentials")) {
            ShipSavedTeleportHooks.register();
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
