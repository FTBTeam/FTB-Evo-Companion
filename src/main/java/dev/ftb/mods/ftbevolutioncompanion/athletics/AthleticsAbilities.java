package dev.ftb.mods.ftbevolutioncompanion.athletics;

import dev.ftb.mods.ftbevolutioncompanion.athletics.network.AthleticsPayloads;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public final class AthleticsAbilities {
    public enum Ability {
        EXTRA_JUMPS("extra_jumps"),
        WALL_CLIMB("wall_climb"),
        AIR_DASH("air_dash");

        private final String key;

        Ability(String key) {
            this.key = key;
        }

        public String key() {
            return key;
        }

        public static Ability byIndex(int index) {
            Ability[] values = values();
            return values[Math.floorMod(index, values.length)];
        }
    }

    private AthleticsAbilities() {
    }

    public static AthleticsToggles toggles(Player player) {
        return player.getData(AthleticsRegistry.TOGGLES);
    }

    public static int maxExtraJumps(Player player) {
        if (!toggles(player).extraJumps()) {
            return 0;
        }
        return (int) player.getAttributeValue(AthleticsRegistry.EXTRA_JUMPS);
    }

    public static int maxAirDashes(Player player) {
        if (!toggles(player).airDash()) {
            return 0;
        }
        return (int) player.getAttributeValue(AthleticsRegistry.AIR_DASH);
    }

    public static boolean isWallClinging(Player player) {
        return player.horizontalCollision
                && player.isShiftKeyDown()
                && !player.onGround()
                && !player.isInWater()
                && !player.isInLava()
                && !player.isFallFlying()
                && !player.isSpectator()
                && !player.isPassenger()
                && !player.getAbilities().flying
                && toggles(player).wallClimb()
                && player.getAttributeValue(AthleticsRegistry.WALL_CLIMB) > 0;
    }

    public static int maxClingTicks(Player player) {
        if (!toggles(player).wallClimb()) {
            return 0;
        }
        return (int) (player.getAttributeValue(AthleticsRegistry.WALL_CLIMB) * 20.0);
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide() && isWallClinging(player)) {
            player.resetFallDistance();
        }
    }

    public static void handleToggle(ServerPlayer player, Ability ability) {
        AthleticsToggles next = toggles(player).toggled(ability);
        player.setData(AthleticsRegistry.TOGGLES, next);
        PacketDistributor.sendToPlayer(player, new AthleticsPayloads.SyncToggles(next));
        String state = next.get(ability) ? "on" : "off";
        player.sendOverlayMessage(
                Component.translatable("ftbevolutioncompanion.athletics.toggle." + ability.key() + "." + state));
    }

    public static void handleAction(ServerPlayer player, Ability ability) {
        if (ability == Ability.EXTRA_JUMPS && maxExtraJumps(player) > 0
                || ability == Ability.AIR_DASH && maxAirDashes(player) > 0) {
            player.resetFallDistance();
        }
    }

    public static void syncToggles(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new AthleticsPayloads.SyncToggles(toggles(player)));
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
