package dev.ftb.mods.ftbevolutioncompanion.compat.powerarmor;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PowerArmorRecipeSync {
    private static final Identifier COMPRESSING = Identifier.fromNamespaceAndPath("power_armor", "compressing");

    private static RecipeMap recipes = RecipeMap.EMPTY;

    public record SyncCompressingRecipes(List<RecipeHolder<?>> recipes) implements CustomPacketPayload {
        public static final Type<SyncCompressingRecipes> TYPE =
                new Type<>(FTBEvolutionCompanion.id("sync_power_armor_recipes"));
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncCompressingRecipes> STREAM_CODEC =
                RecipeHolder.STREAM_CODEC
                        .apply(ByteBufCodecs.list())
                        .map(SyncCompressingRecipes::new, SyncCompressingRecipes::recipes);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private PowerArmorRecipeSync() {}

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                SyncCompressingRecipes.TYPE,
                SyncCompressingRecipes.STREAM_CODEC,
                (payload, context) -> recipes = RecipeMap.create(payload.recipes()));
    }

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        List<RecipeHolder<?>> compressing = event.getPlayerList().getServer().getRecipeManager().getRecipes().stream()
                .filter(holder -> COMPRESSING.equals(BuiltInRegistries.RECIPE_SERIALIZER.getKey(
                        holder.value().getSerializer())))
                .toList();
        SyncCompressingRecipes payload = new SyncCompressingRecipes(compressing);
        event.getRelevantPlayers().forEach(player -> PacketDistributor.sendToPlayer(player, payload));
    }

    public static RecipeMap recipes() {
        return recipes;
    }
}
