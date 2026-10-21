package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.effect.EffectContext;
import com.titammods.hephaestus_tools.effect.HitDetails;
import com.titammods.hephaestus_tools.effect.ToolEffect;
import net.minecraft.world.entity.LivingEntity;

public record IgniteEffect(int seconds, double knockback) implements ToolEffect {
    public static final MapCodec<IgniteEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.optionalFieldOf("seconds", 15).forGetter(IgniteEffect::seconds),
                    Codec.DOUBLE.optionalFieldOf("knockback", 1.0).forGetter(IgniteEffect::knockback))
            .apply(instance, IgniteEffect::new));

    @Override
    public MapCodec<IgniteEffect> codec() {
        return CODEC;
    }

    @Override
    public void afterHit(EffectContext context, HitDetails hit, int level) {
        LivingEntity target = context.target();
        if (target == null || context.level().isClientSide()) {
            return;
        }
        LivingEntity holder = context.holder();
        target.igniteForSeconds(seconds);
        if (knockback > 0.0) {
            target.knockback(knockback, holder.getX() - target.getX(), holder.getZ() - target.getZ());
        }
    }
}
