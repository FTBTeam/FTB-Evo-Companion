package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.titammods.hephaestus_tools.effect.EffectContext;
import com.titammods.hephaestus_tools.effect.HitDetails;
import com.titammods.hephaestus_tools.effect.ToolEffect;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public record FreezeEffect(int seconds, int amplifier) implements ToolEffect {
    private static final Identifier FROZEN = Identifier.fromNamespaceAndPath("iceandfire", "frozen");

    public static final MapCodec<FreezeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.INT.optionalFieldOf("seconds", 15).forGetter(FreezeEffect::seconds),
                    Codec.INT.optionalFieldOf("amplifier", 2).forGetter(FreezeEffect::amplifier))
            .apply(instance, FreezeEffect::new));

    @Override
    public MapCodec<FreezeEffect> codec() {
        return CODEC;
    }

    @Override
    public void afterHit(EffectContext context, HitDetails hit, int level) {
        LivingEntity target = context.target();
        if (target == null || context.level().isClientSide()) {
            return;
        }
        int ticks = seconds * 20;
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks, amplifier));
        target.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, ticks, amplifier));
        BuiltInRegistries.MOB_EFFECT
                .get(FROZEN)
                .ifPresent(frozen -> target.addEffect(new MobEffectInstance(frozen, ticks, 0)));
    }
}
