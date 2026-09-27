package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.client.HemomancyDisplay;

import org.cyclops.evilcraft.api.broom.BroomModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = BroomModifier.class, remap = false)
public abstract class EvilCraftBroomTooltipMixin {
    @ModifyVariable(
            method = "getTooltipLine(Ljava/lang/String;FFZ)Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0)
    private float ftbevo$skillValue(float value) {
        return HemomancyDisplay.broomModifier(value, (BroomModifier) (Object) this);
    }

    @ModifyVariable(
            method = "getTooltipLine(Ljava/lang/String;FFZ)Lnet/minecraft/network/chat/Component;",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1)
    private float ftbevo$skillBonus(float bonusValue) {
        return HemomancyDisplay.broomModifier(bonusValue, (BroomModifier) (Object) this);
    }
}
