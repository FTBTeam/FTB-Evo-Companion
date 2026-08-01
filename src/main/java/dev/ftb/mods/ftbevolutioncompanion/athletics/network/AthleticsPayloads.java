package dev.ftb.mods.ftbevolutioncompanion.athletics.network;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsToggles;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class AthleticsPayloads {
    public record ToggleAbility(int ability) implements CustomPacketPayload {
        public static final Type<ToggleAbility> TYPE = new Type<>(FTBEvolutionCompanion.id("toggle_ability"));
        public static final StreamCodec<ByteBuf, ToggleAbility> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(ToggleAbility::new, ToggleAbility::ability);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record AbilityAction(int ability) implements CustomPacketPayload {
        public static final Type<AbilityAction> TYPE = new Type<>(FTBEvolutionCompanion.id("ability_action"));
        public static final StreamCodec<ByteBuf, AbilityAction> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(AbilityAction::new, AbilityAction::ability);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SyncToggles(AthleticsToggles toggles) implements CustomPacketPayload {
        public static final Type<SyncToggles> TYPE = new Type<>(FTBEvolutionCompanion.id("sync_toggles"));
        public static final StreamCodec<ByteBuf, SyncToggles> STREAM_CODEC =
                AthleticsToggles.STREAM_CODEC.map(SyncToggles::new, SyncToggles::toggles);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private AthleticsPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(ToggleAbility.TYPE, ToggleAbility.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                AthleticsAbilities.handleToggle(player, AthleticsAbilities.Ability.byIndex(payload.ability()));
            }
        });

        registrar.playToServer(AbilityAction.TYPE, AbilityAction.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                AthleticsAbilities.handleAction(player, AthleticsAbilities.Ability.byIndex(payload.ability()));
            }
        });

        registrar.playToClient(SyncToggles.TYPE, SyncToggles.STREAM_CODEC,
                (payload, context) -> context.player().setData(AthleticsRegistry.TOGGLES, payload.toggles()));
    }
}
