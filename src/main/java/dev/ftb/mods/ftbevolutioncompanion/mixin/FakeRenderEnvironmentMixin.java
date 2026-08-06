package dev.ftb.mods.ftbevolutioncompanion.mixin;

import java.lang.ref.WeakReference;
import java.util.Objects;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.CommonListenerCookie;
import net.minecraft.client.multiplayer.chat.ChatAbilities;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.resources.ResourceKey;
import net.minecraft.stats.StatsCounter;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * GuideME 26.1.12-beta rebuilds its entire fake client environment
 * from scratch every frame for every visible game scene. Keeping a structure 
 * page open allocates per second. The churn pins the heap at its cap and
 * presents like a memory leak.
 *
 * Remove once GuideME ships a fix:
 * https://github.com/AppliedEnergistics/GuideME/issues/106
 */
@Mixin(targets = "guideme.internal.scene.FakeRenderEnvironment", remap = false)
public abstract class FakeRenderEnvironmentMixin {

    @Unique
    private static final Object FTBEVO$NO_LEVEL = new Object();

    @Unique
    private static WeakReference<Object> ftbevo$cachedFor = new WeakReference<>(null);

    @Unique
    private static Connection ftbevo$connection;

    @Unique
    private static ClientPacketListener ftbevo$packetListener;

    @Unique
    private static ClientLevel ftbevo$level;

    @Unique
    private static LocalPlayer ftbevo$player;

    @Inject(method = "create", at = @At("HEAD"))
    private static void ftbevo$dropCacheOnLevelChange(CallbackInfoReturnable<Object> cir) {
        Object key = Objects.requireNonNullElse(Minecraft.getInstance().level, FTBEVO$NO_LEVEL);
        if (ftbevo$cachedFor.get() != key) {
            ftbevo$cachedFor = new WeakReference<>(key);
            ftbevo$connection = null;
            ftbevo$packetListener = null;
            ftbevo$level = null;
            ftbevo$player = null;
        }
    }

    @Redirect(
            method = "create",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/network/protocol/PacketFlow;)Lnet/minecraft/network/Connection;"))
    private static Connection ftbevo$cachedConnection(PacketFlow flow) {
        if (ftbevo$connection == null) {
            ftbevo$connection = new Connection(flow);
        }
        return ftbevo$connection;
    }

    @Redirect(
            method = "create",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/Connection;Lnet/minecraft/client/multiplayer/CommonListenerCookie;)Lnet/minecraft/client/multiplayer/ClientPacketListener;"))
    private static ClientPacketListener ftbevo$cachedPacketListener(
            Minecraft minecraft, Connection connection, CommonListenerCookie cookie) {
        if (ftbevo$packetListener == null) {
            ftbevo$packetListener = new ClientPacketListener(minecraft, connection, cookie);
        }
        return ftbevo$packetListener;
    }

    @Redirect(
            method = "create",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/multiplayer/ClientPacketListener;Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/Holder;IILnet/minecraft/client/renderer/LevelRenderer;ZJI)Lnet/minecraft/client/multiplayer/ClientLevel;"))
    private static ClientLevel ftbevo$cachedLevel(
            ClientPacketListener packetListener,
            ClientLevel.ClientLevelData levelData,
            ResourceKey<Level> dimension,
            Holder<DimensionType> dimensionType,
            int viewDistance,
            int serverSimulationDistance,
            LevelRenderer levelRenderer,
            boolean isDebug,
            long biomeZoomSeed,
            int seaLevel) {
        if (ftbevo$level == null) {
            ftbevo$level = new ClientLevel(
                    packetListener,
                    levelData,
                    dimension,
                    dimensionType,
                    viewDistance,
                    serverSimulationDistance,
                    levelRenderer,
                    isDebug,
                    biomeZoomSeed,
                    seaLevel);
        }
        return ftbevo$level;
    }

    @Redirect(
            method = "create",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/ClientPacketListener;Lnet/minecraft/stats/StatsCounter;Lnet/minecraft/client/ClientRecipeBook;Lnet/minecraft/world/entity/player/Input;ZLnet/minecraft/client/multiplayer/chat/ChatAbilities;)Lnet/minecraft/client/player/LocalPlayer;"))
    private static LocalPlayer ftbevo$cachedPlayer(
            Minecraft minecraft,
            ClientLevel level,
            ClientPacketListener packetListener,
            StatsCounter stats,
            ClientRecipeBook recipeBook,
            Input input,
            boolean shiftKeyDown,
            ChatAbilities chatAbilities) {
        if (ftbevo$player == null) {
            ftbevo$player = new LocalPlayer(
                    minecraft, level, packetListener, stats, recipeBook, input, shiftKeyDown, chatAbilities);
        }
        return ftbevo$player;
    }
}
