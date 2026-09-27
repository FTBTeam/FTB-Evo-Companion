package dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import org.jspecify.annotations.Nullable;

public final class OtherworldTooltips {
    public static final String SOUL_THRIFT = "ftbevolutioncompanion.magic.otherworld.soul_thrift";
    public static final String SPELL_COOLDOWN = "ftbevolutioncompanion.magic.otherworld.spell_cooldown";

    private static final int SPELL_SCREEN_COOLDOWN_SHOWN_FROM = 100;

    private OtherworldTooltips() {
    }

    public static int spellCooldown(@Nullable Player player, int cooldown) {
        return player == null ? cooldown : OtherworldMagic.hastenedCooldown(player, cooldown);
    }

    public static @Nullable Component spellScreenCooldown(@Nullable Player player, int cooldown) {
        if (cooldown < SPELL_SCREEN_COOLDOWN_SHOWN_FROM) {
            return null;
        }
        int hastened = spellCooldown(player, cooldown);
        if (hastened == cooldown) {
            return null;
        }
        return Component.translatable(SPELL_COOLDOWN, seconds(hastened)).withStyle(ChatFormatting.DARK_GRAY);
    }

    public static @Nullable Component soulThrift() {
        return soulThrift(Minecraft.getInstance().player);
    }

    public static @Nullable Component soulThrift(@Nullable Player player) {
        double thrift = player == null ? 0.0 : MagicRegistry.value(player, MagicRegistry.ANIMA_SOUL_THRIFT);
        if (thrift <= 0.0) {
            return null;
        }
        return Component.translatable(SOUL_THRIFT, percent(Math.min(thrift, 1.0))).withStyle(ChatFormatting.DARK_AQUA);
    }

    private static String seconds(int ticks) {
        return ticks % 20 == 0 ? Integer.toString(ticks / 20) : Double.toString(ticks / 20.0);
    }

    private static String percent(double fraction) {
        double tenths = Math.round(fraction * 1000.0) / 10.0;
        return tenths == Math.rint(tenths) ? Long.toString((long) tenths) : Double.toString(tenths);
    }
}
