package dev.ftb.mods.ftbevolutioncompanion.spawn;

import com.mojang.serialization.Codec;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public final class WorldSpawnEnforcer {
    private static final Logger LOGGER = LoggerFactory.getLogger("WorldSpawn");

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<Boolean>> PLACED =
            ATTACHMENTS.register("world_spawn_placed",
                    () -> AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL.fieldOf("placed"))
                            .copyOnDeath()
                            .build());

    private WorldSpawnEnforcer() {
    }

    public static void onLevelLoad(LevelEvent.Load event) {
        if (!CompanionConfig.ENFORCE_WORLD_SPAWN.get()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;
        enforce(level);
    }

    public static void onServerStarted(ServerStartedEvent event) {
        if (!CompanionConfig.ENFORCE_WORLD_SPAWN.get()) return;
        enforce(event.getServer().overworld());
    }

    private static void enforce(ServerLevel level) {
        BlockPos spawn = configSpawn();
        LevelData.RespawnData current = level.getRespawnData();
        if (current.pos().equals(spawn) && current.dimension() == Level.OVERWORLD) return;
        level.setRespawnData(LevelData.RespawnData.of(Level.OVERWORLD, spawn, 0.0F, 0.0F));
        LOGGER.info("world spawn set to {}", spawn);
    }

    private static BlockPos configSpawn() {
        return new BlockPos(CompanionConfig.SPAWN_X.get(), CompanionConfig.SPAWN_Y.get(), CompanionConfig.SPAWN_Z.get());
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!CompanionConfig.ENFORCE_WORLD_SPAWN.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getData(PLACED)) return;
        player.setData(PLACED, true);
        if (player.getRespawnConfig() != null) return;
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        Target target = target(server);
        player.teleportTo(target.level(), target.pos().x, target.pos().y, target.pos().z, Set.of(), target.yaw(), target.pitch(), false);
        applySafetyNet(player);
        LOGGER.info("placed new player {} at the world spawn {}", player.getName().getString(), target.pos());
    }

    private static void applySafetyNet(ServerPlayer player) {
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 1200, 0, true, false));
    }

    public static void onRespawnPosition(PlayerRespawnPositionEvent event) {
        if (!CompanionConfig.ENFORCE_WORLD_SPAWN.get()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.getRespawnConfig() != null) return;
        MinecraftServer server = player.level().getServer();
        if (server == null) return;
        Target target = target(server);
        event.setTeleportTransition(new TeleportTransition(target.level(), target.pos(), Vec3.ZERO,
                target.yaw(), target.pitch(), TeleportTransition.DO_NOTHING));
    }

    private static Target target(MinecraftServer server) {
        return new Target(server.overworld(), Vec3.atBottomCenterOf(configSpawn()), 0.0F, 0.0F);
    }

    private record Target(ServerLevel level, Vec3 pos, float yaw, float pitch) {
    }
}
