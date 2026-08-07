package dev.ftb.mods.ftbevolutioncompanion.skills.network;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillToggles;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class SkillsPayloads {
    public record ToggleSkill(int toggle) implements CustomPacketPayload {
        public static final Type<ToggleSkill> TYPE = new Type<>(FTBEvolutionCompanion.id("toggle_skill"));
        public static final StreamCodec<ByteBuf, ToggleSkill> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(ToggleSkill::new, ToggleSkill::toggle);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ActivateSkill(int skill) implements CustomPacketPayload {
        public static final Type<ActivateSkill> TYPE = new Type<>(FTBEvolutionCompanion.id("activate_skill"));
        public static final StreamCodec<ByteBuf, ActivateSkill> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(ActivateSkill::new, ActivateSkill::skill);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SyncSkillToggles(SkillToggles toggles) implements CustomPacketPayload {
        public static final Type<SyncSkillToggles> TYPE = new Type<>(FTBEvolutionCompanion.id("sync_skill_toggles"));
        public static final StreamCodec<ByteBuf, SyncSkillToggles> STREAM_CODEC =
                SkillToggles.STREAM_CODEC.map(SyncSkillToggles::new, SyncSkillToggles::toggles);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private SkillsPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(ToggleSkill.TYPE, ToggleSkill.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                SkillsAbilities.handleToggle(player, SkillToggles.Toggle.byIndex(payload.toggle()));
            }
        });

        registrar.playToServer(ActivateSkill.TYPE, ActivateSkill.STREAM_CODEC, (payload, context) -> {
            if (context.player() instanceof ServerPlayer player) {
                SkillsAbilities.handleActivate(player, payload.skill());
            }
        });

        registrar.playToClient(SyncSkillToggles.TYPE, SyncSkillToggles.STREAM_CODEC,
                (payload, context) -> context.player().setData(SkillsRegistry.TOGGLES, payload.toggles()));
    }
}
