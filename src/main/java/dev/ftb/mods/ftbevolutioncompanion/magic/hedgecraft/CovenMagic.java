package dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.sterner.witchery.content.block.ritual.GoldenRitualChalkBlockEntity;
import dev.sterner.witchery.content.item.WitcheryPotionIngredient;
import dev.sterner.witchery.core.api.SymbologySpell;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

public final class CovenMagic {
    private static final ThreadLocal<Player> RITE_STARTER = new ThreadLocal<>();
    private static final Map<String, Double> LAST_ALTAR_EFFICIENCY = new ConcurrentHashMap<>();

    private CovenMagic() {}

    public static void onServerStopped(ServerStoppedEvent event) {
        LAST_ALTAR_EFFICIENCY.clear();
    }

    public static int witchPower(Player player, int power) {
        if (player == null || player.level().isClientSide()) {
            return power;
        }
        int bonus = HedgeCraftMagic.wholeLevels(MagicRegistry.value(player, MagicRegistry.WITCH_POWER));
        return bonus <= 0 ? power : Math.min(power + bonus, CompanionConfig.WITCH_POWER_CAP.get());
    }

    public static void beginRiteStart(Player player) {
        RITE_STARTER.set(player);
    }

    public static void endRiteStart() {
        RITE_STARTER.remove();
    }

    public static int altarCost(GoldenRitualChalkBlockEntity rite, int amount) {
        if (amount <= 0) {
            return amount;
        }
        double efficiency = altarEfficiency(rite);
        return efficiency <= 0.0 ? amount : (int) Math.ceil(amount * (1.0 - efficiency));
    }

    private static double altarEfficiency(GoldenRitualChalkBlockEntity rite) {
        Player starter = RITE_STARTER.get();
        Player owner = starter != null ? starter : onlineOwner(rite);
        String key = ownerKey(owner != null ? owner.getGameProfile().name() : rite.getOwnerName());
        if (owner != null) {
            double efficiency = MagicRegistry.value(owner, MagicRegistry.WITCHERY_ALTAR_EFFICIENCY);
            if (key != null) {
                LAST_ALTAR_EFFICIENCY.put(key, efficiency);
            }
            return efficiency;
        }
        if (key == null || CompanionConfig.RITUAL_OWNER_OFFLINE_FULL_COST.get()) {
            return 0.0;
        }
        return LAST_ALTAR_EFFICIENCY.getOrDefault(key, 0.0);
    }

    private static String ownerKey(String name) {
        return name == null || name.isEmpty() ? null : name.toLowerCase(Locale.ROOT);
    }

    public static ServerPlayer onlineOwner(GoldenRitualChalkBlockEntity rite) {
        if (!(rite.getLevel() instanceof ServerLevel level)) {
            return null;
        }
        PlayerList players = level.getServer().getPlayerList();
        Player stored = rite.getOwnerNotStored();
        ServerPlayer online = stored == null ? null : players.getPlayer(stored.getUUID());
        if (online != null) {
            return online;
        }
        String name = rite.getOwnerName();
        return name == null || name.isEmpty() ? null : players.getPlayerByName(name);
    }

    public static void awardRite(GoldenRitualChalkBlockEntity rite) {
        ServerPlayer owner = onlineOwner(rite);
        if (owner != null) {
            MagicRegistry.award(owner, MagicRegistry.RITES_PERFORMED);
        }
    }

    public static int infusionGain(Player player, int amount) {
        if (amount <= 0 || player == null || player.level().isClientSide()) {
            return amount;
        }
        double gain = MagicRegistry.value(player, MagicRegistry.WITCHERY_INFUSION_GAIN);
        return gain <= 0.0 ? amount : HedgeCraftMagic.roundRandomly(player.getRandom(), amount * (1.0 + gain));
    }

    public static int infusionSpend(Player player, int amount) {
        if (amount <= 0 || player == null || player.level().isClientSide()) {
            return amount;
        }
        double efficiency = MagicRegistry.value(player, MagicRegistry.WITCHERY_INFUSION_EFFICIENCY);
        return efficiency <= 0.0
                ? amount
                : HedgeCraftMagic.roundRandomly(player.getRandom(), amount * (1.0 - efficiency));
    }

    public static float extraBottleChance(Player player, float chance) {
        double bonus = MagicRegistry.value(player, MagicRegistry.WITCHERY_BREW_BOTTLING);
        return bonus <= 0.0 ? chance : chance + (float) bonus;
    }

    public static void awardBottle(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            MagicRegistry.award(serverPlayer, MagicRegistry.BREWS_BOTTLED);
        }
    }

    public static WitcheryPotionIngredient.EffectModifier brewModifier(
            Entity entity, WitcheryPotionIngredient.EffectModifier modifier) {
        if (modifier == null || !(entity instanceof LivingEntity living)) {
            return modifier;
        }
        int bonus = HedgeCraftMagic.wholeLevels(MagicRegistry.value(living, MagicRegistry.WITCHERY_BREW_POTENCY));
        if (bonus <= 0) {
            return modifier;
        }
        return new WitcheryPotionIngredient.EffectModifier(
                modifier.getPowerAddition() + bonus, modifier.getDurationAddition(), modifier.getDurationMultiplier());
    }

    public static int symbolLevel(ServerPlayer player, SymbologySpell spell, int level) {
        int bonus = HedgeCraftMagic.wholeLevels(MagicRegistry.value(player, MagicRegistry.WITCHERY_SYMBOL_MASTERY));
        if (bonus <= 0) {
            return level;
        }
        return Math.max(level, Math.min(level + bonus, spell.getMaxLevel()));
    }

    public static void awardSpell(ServerPlayer player) {
        MagicRegistry.award(player, MagicRegistry.SPELLS_CAST);
    }

    public static boolean sparePoppet(LivingEntity owner) {
        if (owner == null || owner.level().isClientSide()) {
            return false;
        }
        double chance = MagicRegistry.value(owner, MagicRegistry.WITCHERY_POPPET_DURABILITY);
        return chance > 0.0 && owner.getRandom().nextDouble() < chance;
    }
}
