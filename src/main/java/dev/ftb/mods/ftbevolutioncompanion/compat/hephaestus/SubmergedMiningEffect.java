package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.mojang.serialization.MapCodec;
import com.titammods.hephaestus_tools.effect.EffectContext;
import com.titammods.hephaestus_tools.effect.ToolEffect;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;

public record SubmergedMiningEffect() implements ToolEffect {
    public static final SubmergedMiningEffect INSTANCE = new SubmergedMiningEffect();
    public static final MapCodec<SubmergedMiningEffect> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public MapCodec<SubmergedMiningEffect> codec() {
        return CODEC;
    }

    @Override
    public float modifyBreakSpeed(EffectContext context, float speed, int level) {
        LivingEntity holder = context.holder();
        if (!holder.isEyeInFluid(FluidTags.WATER)) {
            return speed;
        }
        double submerged = holder.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
        if (submerged > 0.0 && submerged < 1.0) {
            speed /= (float) submerged;
        }
        if (!holder.onGround()) {
            speed *= 5.0F;
        }
        return speed;
    }
}
