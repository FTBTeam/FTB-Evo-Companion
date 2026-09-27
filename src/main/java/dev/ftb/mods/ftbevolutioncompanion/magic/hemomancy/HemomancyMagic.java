package dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy;

import com.breakinblocks.neovitae.common.event.SentientArmourEvent;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.sterner.witchery.core.api.event.VampireAfflictionEvent;
import dev.sterner.witchery.core.api.event.WerewolfAfflictionEvent;
import dev.sterner.witchery_forbidden_magic.core.api.event.LichdomEvent;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HemomancyMagic {
    private static final Map<UUID, Double> OFFLINE_RITUAL_EFFICIENCY = new ConcurrentHashMap<>();

    private HemomancyMagic() {
    }

    public static void register(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onSentientExpGain);
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onVampireLevelChanged);
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onWerewolfLevelChanged);
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onLichLevelChanged);
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(HemomancyMagic::onServerStopped);
    }

    public static double ritualEfficiency(@Nullable Level level, @Nullable UUID owner) {
        if (owner == null) {
            return 0.0;
        }
        MinecraftServer server = level == null ? null : level.getServer();
        ServerPlayer player = server == null ? null : server.getPlayerList().getPlayer(owner);
        if (player != null) {
            double value = MagicRegistry.value(player, MagicRegistry.VITAE_RITUAL_EFFICIENCY);
            rememberRitualEfficiency(owner, value);
            return value;
        }
        if (CompanionConfig.RITUAL_OWNER_OFFLINE_FULL_COST.get()) {
            return 0.0;
        }
        return OFFLINE_RITUAL_EFFICIENCY.getOrDefault(owner, 0.0);
    }

    private static void rememberRitualEfficiency(UUID owner, double value) {
        if (value > 0.0) {
            OFFLINE_RITUAL_EFFICIENCY.put(owner, value);
        } else {
            OFFLINE_RITUAL_EFFICIENCY.remove(owner);
        }
    }

    private static void onSentientExpGain(SentientArmourEvent.ExpGain event) {
        if (event.isTomeExp() || event.getCurrentAmount() <= 0.0F) {
            return;
        }
        double growth = MagicRegistry.value(event.getWearer(), MagicRegistry.VITAE_SENTIENT_GROWTH);
        if (growth <= 0.0) {
            return;
        }
        event.setCurrentAmount((float) (event.getCurrentAmount() * (1.0 + growth)));
    }

    private static void onVampireLevelChanged(VampireAfflictionEvent.LevelChanged event) {
        awardAfflictionLevels(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
    }

    private static void onWerewolfLevelChanged(WerewolfAfflictionEvent.LevelChanged event) {
        awardAfflictionLevels(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
    }

    private static void onLichLevelChanged(LichdomEvent.LevelChanged event) {
        awardAfflictionLevels(event.getPlayer(), event.getOldLevel(), event.getNewLevel());
    }

    private static void awardAfflictionLevels(@Nullable ServerPlayer player, int oldLevel, int newLevel) {
        if (player == null) {
            return;
        }
        for (int level = Math.max(0, oldLevel); level < newLevel; level++) {
            MagicRegistry.award(player, MagicRegistry.AFFLICTION_LEVELS);
        }
    }

    private static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            rememberRitualEfficiency(player.getUUID(), MagicRegistry.value(player, MagicRegistry.VITAE_RITUAL_EFFICIENCY));
        }
    }

    private static void onServerStopped(ServerStoppedEvent event) {
        OFFLINE_RITUAL_EFFICIENCY.clear();
    }
}
