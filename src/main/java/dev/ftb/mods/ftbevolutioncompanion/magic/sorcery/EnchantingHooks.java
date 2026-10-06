package dev.ftb.mods.ftbevolutioncompanion.magic.sorcery;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public final class EnchantingHooks {
    private static final float MAX_ARCANA = 100.0F;

    private EnchantingHooks() {}

    public static EnchantmentTableStats addArcana(Player player, EnchantmentTableStats stats) {
        if (!(player instanceof ServerPlayer) || stats == null || stats.arcana() >= MAX_ARCANA) {
            return stats;
        }
        double bonus = MagicRegistry.value(player, MagicRegistry.ENCHANTING_ARCANA);
        if (bonus <= 0.0) {
            return stats;
        }
        float arcana = Math.min(MAX_ARCANA, stats.arcana() + (float) bonus);
        return new EnchantmentTableStats(
                stats.tableEterna(),
                stats.quanta(),
                arcana,
                stats.clues(),
                stats.blacklist(),
                stats.treasure(),
                stats.stable());
    }

    public static int discount(LivingEntity entity, int cost) {
        if (cost <= 0) {
            return cost;
        }
        double discount = MagicRegistry.value(entity, MagicRegistry.ENCHANTING_DISCOUNT);
        return discount > 0.0 ? Math.max(0, (int) Math.ceil(cost * (1.0 - discount) - 1.0E-6)) : cost;
    }
}
