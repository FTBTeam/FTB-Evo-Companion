package dev.ftb.mods.ftbevolutioncompanion.pyramid.network;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.client.PyramidClient;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.EvolutionPyramidBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.PyramidQuests;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

public final class PyramidPayloads {
    private static final double REACH = 16.0D;

    public record OpenPyramidScreen(BlockPos pos, long chapterId) implements CustomPacketPayload {
        public static final Type<OpenPyramidScreen> TYPE = new Type<>(FTBEvolutionCompanion.id("open_pyramid_screen"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenPyramidScreen> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, OpenPyramidScreen::pos,
                ByteBufCodecs.VAR_LONG, OpenPyramidScreen::chapterId,
                OpenPyramidScreen::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record SelectPyramidTask(BlockPos pos, long taskId) implements CustomPacketPayload {
        public static final Type<SelectPyramidTask> TYPE = new Type<>(FTBEvolutionCompanion.id("select_pyramid_task"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SelectPyramidTask> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, SelectPyramidTask::pos,
                ByteBufCodecs.VAR_LONG, SelectPyramidTask::taskId,
                SelectPyramidTask::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record LaunchPyramid(BlockPos pos, long questId) implements CustomPacketPayload {
        public static final Type<LaunchPyramid> TYPE = new Type<>(FTBEvolutionCompanion.id("launch_pyramid"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LaunchPyramid> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, LaunchPyramid::pos,
                ByteBufCodecs.VAR_LONG, LaunchPyramid::questId,
                LaunchPyramid::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private PyramidPayloads() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(OpenPyramidScreen.TYPE, OpenPyramidScreen.STREAM_CODEC,
                (payload, context) -> PyramidClient.openScreen(payload.pos(), payload.chapterId()));
        registrar.playToServer(SelectPyramidTask.TYPE, SelectPyramidTask.STREAM_CODEC,
                (payload, context) -> machine(context, payload.pos()).ifPresent(machine -> machine.selectTask(payload.taskId())));
        registrar.playToServer(LaunchPyramid.TYPE, LaunchPyramid.STREAM_CODEC,
                (payload, context) -> machine(context, payload.pos()).ifPresent(machine ->
                        machine.launch((ServerPlayer) context.player(), payload.questId())));
    }

    private static Optional<EvolutionPyramidBlockEntity> machine(IPayloadContext context, BlockPos pos) {
        if (!(context.player() instanceof ServerPlayer player)) return Optional.empty();
        if (!player.level().isLoaded(pos) || player.position().distanceToSqr(pos.getCenter()) > REACH * REACH) return Optional.empty();
        if (!(player.level().getBlockEntity(pos) instanceof EvolutionPyramidBlockEntity machine)) return Optional.empty();
        return PyramidQuests.isMember(player, machine.getOwner()) ? Optional.of(machine) : Optional.empty();
    }
}
