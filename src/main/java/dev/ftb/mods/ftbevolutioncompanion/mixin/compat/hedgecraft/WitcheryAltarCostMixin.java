package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;

import dev.sterner.witchery.content.block.AltarLinkedBlockEntity;
import dev.sterner.witchery.content.block.ritual.GoldenRitualChalkBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = AltarLinkedBlockEntity.class, remap = false)
public abstract class WitcheryAltarCostMixin {
    @ModifyVariable(method = "consumePower(IZ)Z", at = @At("HEAD"), argsOnly = true)
    private int ftbevo$discountRite(int amount) {
        return (Object) this instanceof GoldenRitualChalkBlockEntity rite ? CovenMagic.altarCost(rite, amount) : amount;
    }
}
