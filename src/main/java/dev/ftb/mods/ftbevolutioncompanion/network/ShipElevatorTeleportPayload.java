package dev.ftb.mods.ftbevolutioncompanion.network;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ShipElevatorTeleportPayload(BlockPos from, BlockPos to) implements CustomPacketPayload {
    public static final Type<ShipElevatorTeleportPayload> TYPE =
            new Type<>(FTBEvolutionCompanion.id("ship_elevator_teleport"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShipElevatorTeleportPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, ShipElevatorTeleportPayload::from,
                    BlockPos.STREAM_CODEC, ShipElevatorTeleportPayload::to,
                    ShipElevatorTeleportPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
