package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery;

import at.minecraftschurli.mods.arsmagicalegacy.api.spell.SpellStat;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class ArsMagicaHooks {
    private static final Identifier DURATION = Identifier.fromNamespaceAndPath("arsmagicalegacy", "duration");
    private static final Identifier HEALING = Identifier.fromNamespaceAndPath("arsmagicalegacy", "healing");

    private ArsMagicaHooks() {
    }

    public static double spellPower(LivingEntity caster, SpellStat stat, double value) {
        if (caster == null || stat == null) {
            return value;
        }
        Identifier id = stat.id();
        if (!DURATION.equals(id) && !HEALING.equals(id)) {
            return value;
        }
        double power = MagicRegistry.value(caster, MagicRegistry.ARS_SPELL_POWER);
        return power > 0.0 ? value * (1.0 + power) : value;
    }

    public static double xpGain(Player player, double xp) {
        if (xp <= 0.0) {
            return xp;
        }
        double gain = MagicRegistry.value(player, MagicRegistry.ARS_XP_GAIN);
        return gain > 0.0 ? xp * (1.0 + gain) : xp;
    }
}
