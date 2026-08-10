package dev.ftb.mods.ftbevolutioncompanion.network;

import dev.ftb.mods.ftbevolutioncompanion.compat.elevatorid.ShipElevatorTeleport;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class ServerPayloadHandler {
    private ServerPayloadHandler() {}

    public static void handleShipElevatorTeleport(ShipElevatorTeleportPayload payload, IPayloadContext context) {
        if (!ModList.get().isLoaded("elevatorid")) return;
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ShipElevatorTeleport.teleport(player, payload.from(), payload.to());
            }
        });
    }
}
