package dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy;

import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.breakinblocks.neovitae.ritual.RitualHelper;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cyclops.evilcraft.api.broom.BroomModifier;
import org.jspecify.annotations.Nullable;

public final class HemomancyHooks {
    private static final Identifier BROOM_SPEED = Identifier.fromNamespaceAndPath("evilcraft", "speed");
    private static final double WHOLE_EPSILON = 1.0E-6;

    private HemomancyHooks() {}

    public static int roundRandomly(double value, RandomSource random) {
        int whole = (int) Math.floor(value);
        return random.nextDouble() < value - whole ? whole + 1 : whole;
    }

    public static int ritualUpkeep(int cost, RitualHelper.RitualContext context) {
        if (cost <= 0 || context == null) {
            return cost;
        }
        Level level = context.level();
        if (level == null || level.isClientSide()) {
            return cost;
        }
        double efficiency =
                HemomancyMagic.ritualEfficiency(level, context.master().getOwner());
        if (efficiency <= 0.0) {
            return cost;
        }
        return roundRandomly(cost * (1.0 - efficiency), level.getRandom());
    }

    public static int ritualActivation(int cost, @Nullable Player player) {
        if (cost <= 0 || player == null) {
            return cost;
        }
        double efficiency = MagicRegistry.value(player, MagicRegistry.VITAE_RITUAL_EFFICIENCY);
        if (efficiency <= 0.0) {
            return cost;
        }
        return (int) Math.ceil(cost * (1.0 - efficiency));
    }

    public static @Nullable IAnima animusNetwork(@Nullable IAnima network, @Nullable IMasterRitualStone stone) {
        if (network == null || stone == null || network instanceof DiscountedAnima) {
            return network;
        }
        Level level = stone.getLevel();
        if (level == null || level.isClientSide()) {
            return network;
        }
        double efficiency = HemomancyMagic.ritualEfficiency(level, stone.getOwner());
        if (efficiency <= 0.0) {
            return network;
        }
        return new DiscountedAnima(network, efficiency, level.getRandom());
    }

    public static int bloodConsume(int amount, @Nullable Player player) {
        if (amount <= 0 || player == null) {
            return amount;
        }
        double efficiency = MagicRegistry.value(player, MagicRegistry.EVILCRAFT_BLOOD_EFFICIENCY);
        if (efficiency <= 0.0) {
            return amount;
        }
        if (player.level().isClientSide()) {
            return bloodCheck(amount, player);
        }
        RandomSource random = player.getRandom();
        if (amount <= 2) {
            return random.nextDouble() < efficiency ? 0 : amount;
        }
        return Math.max(1, roundRandomly(amount * (1.0 - efficiency), random));
    }

    public static int bloodCheck(int amount, @Nullable Player player) {
        if (amount <= 2 || player == null) {
            return amount;
        }
        double efficiency = MagicRegistry.value(player, MagicRegistry.EVILCRAFT_BLOOD_EFFICIENCY);
        if (efficiency <= 0.0) {
            return amount;
        }
        return Math.max(1, (int) Math.ceil(amount * (1.0 - efficiency)));
    }

    public static double bloodEfficiency(@Nullable Player player) {
        return player == null ? 0.0 : MagicRegistry.value(player, MagicRegistry.EVILCRAFT_BLOOD_EFFICIENCY);
    }

    public static int bloodHarvest(int amount, @Nullable Player player) {
        if (amount <= 0 || player == null) {
            return amount;
        }
        double harvest = MagicRegistry.value(player, MagicRegistry.EVILCRAFT_BLOOD_HARVEST);
        if (harvest <= 0.0) {
            return amount;
        }
        return (int) Math.round(amount * (1.0 + harvest));
    }

    public static float broomModifier(float value, @Nullable BroomModifier modifier, @Nullable LivingEntity rider) {
        if (value <= 0.0F || modifier == null || rider == null || !BROOM_SPEED.equals(modifier.getId())) {
            return value;
        }
        double speed = MagicRegistry.value(rider, MagicRegistry.EVILCRAFT_BROOM_SPEED);
        if (speed <= 0.0) {
            return value;
        }
        return (float) (value * (1.0 + speed));
    }

    public static int spiritBinding(@Nullable Entity owner, RandomSource random) {
        if (!(owner instanceof Player player)) {
            return 0;
        }
        double binding = MagicRegistry.value(player, MagicRegistry.EVILCRAFT_SPIRIT_BINDING);
        if (binding <= 0.0) {
            return 0;
        }
        return roundRandomly(binding, random);
    }

    public static int abilityCooldown(int cooldown, @Nullable Player player) {
        if (cooldown <= 0 || player == null) {
            return cooldown;
        }
        double reduction = MagicRegistry.value(player, MagicRegistry.WITCHERY_ABILITY_COOLDOWN);
        if (reduction <= 0.0) {
            return cooldown;
        }
        return Math.max(1, (int) Math.round(cooldown * (1.0 - reduction)));
    }

    public static int necroRadius(int radius, @Nullable Player summoner) {
        if (radius <= 0 || summoner == null) {
            return radius;
        }
        double necromancy = MagicRegistry.value(summoner, MagicRegistry.WITCHERY_NECROMANCY);
        if (necromancy <= 0.0) {
            return radius;
        }
        return (int) Math.round(radius * (1.0 + necromancy));
    }

    public static long necroLifetime(long ticks, @Nullable Player summoner) {
        if (ticks <= 0L || summoner == null) {
            return ticks;
        }
        double necromancy = MagicRegistry.value(summoner, MagicRegistry.WITCHERY_NECROMANCY);
        if (necromancy <= 0.0) {
            return ticks;
        }
        return Math.round(ticks * (1.0 + necromancy));
    }

    public static int lichSouls(int souls, @Nullable Player player) {
        if (souls <= 0 || player == null) {
            return souls;
        }
        int bonus = (int) Math.floor(MagicRegistry.value(player, MagicRegistry.WITCHERY_LICH_SOULS) + WHOLE_EPSILON);
        return bonus <= 0 ? souls : souls + bonus;
    }
}
