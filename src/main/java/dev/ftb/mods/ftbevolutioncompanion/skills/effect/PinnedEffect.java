package dev.ftb.mods.ftbevolutioncompanion.skills.effect;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PinnedEffect extends MobEffect {
    public PinnedEffect() {
        super(MobEffectCategory.HARMFUL, 0x6B4F2A);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, FTBEvolutionCompanion.id("pinned_movement"),
                -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.JUMP_STRENGTH, FTBEvolutionCompanion.id("pinned_jump"),
                -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
