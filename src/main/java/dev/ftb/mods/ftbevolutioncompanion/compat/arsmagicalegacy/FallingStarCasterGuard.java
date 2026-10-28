package dev.ftb.mods.ftbevolutioncompanion.compat.arsmagicalegacy;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class FallingStarCasterGuard {
    private static final ResourceKey<DamageType> FALLING_STAR = ResourceKey.create(
            Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("arsmagicalegacy", "falling_star"));

    private FallingStarCasterGuard() {}

    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().is(FALLING_STAR) && event.getSource().getEntity() == event.getEntity()) {
            event.setCanceled(true);
        }
    }
}
