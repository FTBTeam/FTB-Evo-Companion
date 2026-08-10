package dev.ftb.mods.ftbevolutioncompanion.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ModPayloads {
    private ModPayloads() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(
                ShipElevatorTeleportPayload.TYPE,
                ShipElevatorTeleportPayload.STREAM_CODEC,
                ServerPayloadHandler::handleShipElevatorTeleport
        );
    }
}
