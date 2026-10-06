package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client;

import at.minecraftschurli.mods.arsmagicalegacy.api.event.ManaBurnoutCostEvent;
import at.minecraftschurli.mods.arsmagicalegacy.api.spell.Spell;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.SorceryMagic;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ThaumaturgeHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public final class SorceryClientDisplay {
    private static final double NOISE_SCALE = 1.0E6;

    private SorceryClientDisplay() {}

    public static double arsManaCost(Spell spell, double mana) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return mana;
        }
        ManaBurnoutCostEvent event = new ManaBurnoutCostEvent(player, spell, mana, 0.0);
        SorceryMagic.onManaBurnoutCost(event);
        double cost = event.getMana();
        return cost == mana ? mana : Math.round(cost * NOISE_SCALE) / NOISE_SCALE;
    }

    public static float thaumFocusPower(float power) {
        double bonus = MagicRegistry.value(Minecraft.getInstance().player, MagicRegistry.THAUM_FOCUS_POWER);
        return bonus > 0.0 ? power * (float) (1.0 + bonus) : power;
    }

    public static int thaumInstability(int instability) {
        return ThaumaturgeHooks.stabilize(Minecraft.getInstance().player, instability);
    }

    public static Player thaumVisPayer(Player player) {
        return player != null ? player : Minecraft.getInstance().player;
    }
}
