package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import at.minecraftschurli.mods.arsmagicalegacy.api.spell.SpellCastContext;
import at.minecraftschurli.mods.arsmagicalegacy.api.spell.SpellStat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ArsMagicaHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.apiimpl.SpellHelperImpl", remap = false)
public abstract class ArsSpellPowerMixin {
    @ModifyReturnValue(
            method = "getModifiedStat(DLat/minecraftschurli/mods/arsmagicalegacy/api/spell/SpellStat;"
                    + "Ljava/util/List;Lat/minecraftschurli/mods/arsmagicalegacy/api/spell/SpellCastContext;)D",
            at = @At("RETURN"))
    private double ftbevo$scaleSpellPower(double original, @Local(argsOnly = true) SpellStat stat,
                                          @Local(argsOnly = true) SpellCastContext context) {
        return ArsMagicaHooks.spellPower(context == null ? null : context.caster(), stat, original);
    }
}
