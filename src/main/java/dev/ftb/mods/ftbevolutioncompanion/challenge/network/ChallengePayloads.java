package dev.ftb.mods.ftbevolutioncompanion.challenge.network;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeClientData;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeSnapshot;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class ChallengePayloads {
    public record SyncChallengeBoard(ChallengeSnapshot snapshot) implements CustomPacketPayload {
        public static final Type<SyncChallengeBoard> TYPE = new Type<>(FTBEvolutionCompanion.id("sync_challenge_board"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncChallengeBoard> STREAM_CODEC =
                ChallengeSnapshot.STREAM_CODEC.map(SyncChallengeBoard::new, SyncChallengeBoard::snapshot);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private ChallengePayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(SyncChallengeBoard.TYPE, SyncChallengeBoard.STREAM_CODEC,
                (payload, context) -> ChallengeClientData.accept(payload.snapshot()));
    }
}
