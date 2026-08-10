package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid.ShipElevators;
import dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid.client.ShipElevatorClientHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;

public final class ClientBootstrap {
    private ClientBootstrap() {}

    public static void init(IEventBus eventBus) {
        if (ModList.get().isLoaded(ShipElevators.MOD_ID) && ModList.get().isLoaded("sable")) {
            NeoForge.EVENT_BUS.addListener(ShipElevatorClientHandler::onClientTick);
        }
    }
}
