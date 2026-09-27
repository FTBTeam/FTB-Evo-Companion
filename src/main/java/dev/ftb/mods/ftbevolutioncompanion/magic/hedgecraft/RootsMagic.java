package dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;

import elucent.rootsclassic.component.ComponentBase;
import elucent.rootsclassic.component.components.ComponentFlareOrchid;
import elucent.rootsclassic.component.components.ComponentLilac;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class RootsMagic {
    private static final int MIN_RITUAL_TICKS = 20;

    private RootsMagic() {
    }

    public static int potencyBonus(ComponentBase component, Entity caster, int basePotency) {
        if (component instanceof ComponentFlareOrchid || !(caster instanceof LivingEntity living)) {
            return 0;
        }
        int bonus = HedgeCraftMagic.wholeLevels(MagicRegistry.value(living, MagicRegistry.ROOTS_POTENCY));
        if (bonus <= 0) {
            return 0;
        }
        int cap = CompanionConfig.ROOTS_POTENCY_CAP.get();
        if (component instanceof ComponentLilac) {
            cap = Math.min(cap, CompanionConfig.ROOTS_LILAC_POTENCY_CAP.get());
        }
        return Math.max(0, Math.min(bonus, cap - basePotency));
    }

    public static float manaCost(LivingEntity caster, float cost) {
        double reduction = MagicRegistry.value(caster, MagicRegistry.ROOTS_COST_REDUCTION);
        return reduction <= 0.0 ? cost : (float) (cost * (1.0 - reduction));
    }

    public static int remainingStaffUses(LivingEntity caster, int remaining) {
        if (!(caster instanceof ServerPlayer player)) {
            return remaining;
        }
        double chance = MagicRegistry.value(player, MagicRegistry.ROOTS_STAFF_SAVE);
        if (chance <= 0.0 || player.getRandom().nextDouble() >= chance) {
            return remaining;
        }
        player.containerMenu.sendAllDataToRemote();
        return remaining + 1;
    }

    public static int ritualTicks(Player player, int ticks) {
        double speed = MagicRegistry.value(player, MagicRegistry.ROOTS_RITUAL_SPEED);
        if (speed <= 0.0) {
            return ticks;
        }
        return Math.min(ticks, Math.max(MIN_RITUAL_TICKS, (int) Math.round(ticks * (1.0 - speed))));
    }

    public static void awardCast(Entity caster) {
        if (caster instanceof ServerPlayer player) {
            MagicRegistry.award(player, MagicRegistry.SPELLS_CAST);
        }
    }

    public static void awardRite(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MagicRegistry.award(serverPlayer, MagicRegistry.RITES_PERFORMED);
        }
    }
}
