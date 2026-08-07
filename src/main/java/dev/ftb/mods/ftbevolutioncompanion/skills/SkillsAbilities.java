package dev.ftb.mods.ftbevolutioncompanion.skills;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.skills.network.SkillsPayloads;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class SkillsAbilities {
    public static final int ACTIVATE_NINJA = 0;

    private SkillsAbilities() {
    }

    public static SkillToggles toggles(Player player) {
        return player.getData(SkillsRegistry.TOGGLES);
    }

    public static void handleToggle(ServerPlayer player, SkillToggles.Toggle toggle) {
        SkillToggles next = toggles(player).toggled(toggle);
        player.setData(SkillsRegistry.TOGGLES, next);
        PacketDistributor.sendToPlayer(player, new SkillsPayloads.SyncSkillToggles(next));
        String state = next.get(toggle) ? "on" : "off";
        player.sendOverlayMessage(
                Component.translatable("ftbevolutioncompanion.skills.toggle." + toggle.key() + "." + state));
    }

    public static void handleActivate(ServerPlayer player, int skill) {
        if (skill == ACTIVATE_NINJA) {
            activateNinja(player);
        }
    }

    private static void activateNinja(ServerPlayer player) {
        double seconds = SkillsHelper.attr(player, SkillsRegistry.NINJA);
        if (seconds <= 0.0) {
            return;
        }
        if (!SkillCooldowns.ready(player, SkillCooldowns.NINJA)) {
            sendCooldown(player, "attribute.name.ftb.ninja", SkillCooldowns.remaining(player, SkillCooldowns.NINJA));
            return;
        }
        int ticks = (int) (seconds * 20.0);
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, ticks, 0, false, false, true));
        player.getData(SkillsRegistry.COMBAT_STATE).ninjaUntil = player.level().getGameTime() + ticks;
        SkillCooldowns.start(player, SkillCooldowns.NINJA, CompanionConfig.NINJA_COOLDOWN.get());
        player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.ninja.activated"));
    }

    public static void sendCooldown(ServerPlayer player, String nameKey, long remainingTicks) {
        player.sendOverlayMessage(Component.translatable("ftbevolutioncompanion.skills.cooldown",
                Component.translatable(nameKey), String.valueOf(remainingTicks / 20)));
    }

    public static void syncToggles(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new SkillsPayloads.SyncSkillToggles(toggles(player)));
    }

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncToggles(player);
        }
    }
}
