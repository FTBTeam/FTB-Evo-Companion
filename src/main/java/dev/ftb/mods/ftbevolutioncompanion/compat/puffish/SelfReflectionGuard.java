package dev.ftb.mods.ftbevolutioncompanion.compat.puffish;

import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class SelfReflectionGuard {
    private SelfReflectionGuard() {}

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().is(DamageTypes.THORNS) && event.getSource().getEntity() == event.getEntity()) {
            event.setCanceled(true);
        }
    }
}
