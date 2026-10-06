package dev.ftb.mods.ftbevolutioncompanion.magic.otherworld;

import dev.anima.Anima;
import dev.anima.Saronite;
import dev.anima.SoulShades;
import dev.anima.SoulType;
import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

public final class AnimaReaping {
    private AnimaReaping() {}

    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !(mob.level() instanceof ServerLevel)) {
            return;
        }
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        double reaping = MagicRegistry.value(player, MagicRegistry.ANIMA_REAPING);
        if (reaping <= 0.0 || SoulShades.isSpirit(mob) || !SoulType.hasSoul(mob)) {
            return;
        }
        boolean melee = source.getDirectEntity() == player
                && source.is(DamageTypes.PLAYER_ATTACK)
                && Saronite.isSword(player.getMainHandItem());
        boolean saroniteArrow = source.getDirectEntity() instanceof AbstractArrow arrow
                && arrow.getPickupItemStackOrigin().is(Anima.item("saronite_arrow"));
        if (!melee && (!saroniteArrow || SoulType.isGod(mob))) {
            return;
        }
        int extra = (int) Math.floor(reaping);
        if (player.getRandom().nextDouble() < reaping - extra) {
            extra++;
        }
        int bottled = 0;
        while (bottled < extra && Saronite.bottle(player, SoulType.bottle(mob))) {
            bottled++;
        }
    }
}
