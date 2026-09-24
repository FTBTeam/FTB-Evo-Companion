package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class FabricatorPayloads {
    private FabricatorPayloads() {}
    public record Transfer(int menuId, Identifier recipe, boolean maximum) implements CustomPacketPayload {
        public static final Type<Transfer> TYPE = new Type<>(FTBEvolutionCompanion.id("fabricator_transfer"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Transfer> CODEC = StreamCodec.composite(
                ByteBufCodecs.VAR_INT, Transfer::menuId, Identifier.STREAM_CODEC, Transfer::recipe,
                ByteBufCodecs.BOOL, Transfer::maximum, Transfer::new);
        @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
    }
    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Transfer.TYPE, Transfer.CODEC, (payload, context) -> {
            if (!(context.player() instanceof ServerPlayer player)
                    || !(player.containerMenu instanceof FabricatorMenu menu)
                    || menu.containerId != payload.menuId || !menu.stillValid(player)) return;
            player.level().recipeAccess().recipeMap().byType(FabricatorRegistry.RECIPE_TYPE.get()).stream()
                    .filter(holder -> holder.id().identifier().equals(payload.recipe)).findFirst().ifPresent(holder -> {
                        FabricatorTransfer.Plan plan = FabricatorTransfer.plan(menu, player.getInventory(), holder.value(), payload.maximum);
                        if (plan != null) FabricatorTransfer.apply(menu, player.getInventory(), plan);
                    });
        });
    }
}
