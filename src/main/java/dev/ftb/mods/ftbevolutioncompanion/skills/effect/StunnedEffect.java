package dev.ftb.mods.ftbevolutioncompanion.skills.effect;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class StunnedEffect extends MobEffect {
    public StunnedEffect() {
        super(MobEffectCategory.HARMFUL, 0x8E9BA6);
        addAttributeModifier(Attributes.MOVEMENT_SPEED, FTBEvolutionCompanion.id("stunned_movement"),
                -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        addAttributeModifier(Attributes.ATTACK_SPEED, FTBEvolutionCompanion.id("stunned_attack"),
                -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
}
