package dev.ftb.mods.ftbevolutioncompanion.skills.effect;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedingEffect extends MobEffect {
    public BleedingEffect() {
        super(MobEffectCategory.HARMFUL, 0x8A0303);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplification) {
        float damage = mob.getMaxHealth() * CompanionConfig.BLEED_FRACTION.get().floatValue() * (amplification + 1);
        if (damage > 0.0F) {
            mob.hurtServer(level, mob.damageSources().magic(), damage);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        int interval = CompanionConfig.BLEED_INTERVAL_TICKS.get();
        return interval <= 0 || tickCount % interval == 0;
    }
}
