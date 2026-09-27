package dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import dev.sterner.witchery.core.api.IAbility;
import dev.sterner.witchery.feature.ability.AbilityCooldownManager;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import org.cyclops.evilcraft.api.broom.BroomModifier;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;

public final class HemomancyDisplay {
    private static final DecimalFormat EV_FORMAT = new DecimalFormat("#,##0.##");

    private HemomancyDisplay() {
    }

    private static @Nullable Player player() {
        return Minecraft.getInstance().player;
    }

    public static int ritualActivation(int cost) {
        return HemomancyHooks.ritualActivation(cost, player());
    }

    public static @Nullable String ritualUpkeep(long cost) {
        Player player = player();
        if (cost <= 0L || player == null) {
            return null;
        }
        double efficiency = MagicRegistry.value(player, MagicRegistry.VITAE_RITUAL_EFFICIENCY);
        if (efficiency <= 0.0) {
            return null;
        }
        return EV_FORMAT.format(cost * (1.0 - efficiency));
    }

    public static float broomModifier(float value, BroomModifier modifier) {
        return HemomancyHooks.broomModifier(value, modifier, player());
    }

    public static int abilityCooldown(int cooldown, IAbility ability, @Nullable Player player) {
        if (player == null) {
            return cooldown;
        }
        int reduced = HemomancyHooks.abilityCooldown(cooldown, player);
        if (reduced >= cooldown) {
            return cooldown;
        }
        return Math.max(reduced, AbilityCooldownManager.INSTANCE.getCooldown(player, ability));
    }
}
