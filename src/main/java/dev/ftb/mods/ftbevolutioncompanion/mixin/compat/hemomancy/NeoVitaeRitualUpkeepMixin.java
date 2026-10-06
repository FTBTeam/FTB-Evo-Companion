package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.breakinblocks.neovitae.ritual.RitualHelper;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = RitualHelper.class, remap = false)
public abstract class NeoVitaeRitualUpkeepMixin {
    @ModifyVariable(
            method = "syphonEV(Lcom/breakinblocks/neovitae/ritual/RitualHelper$RitualContext;I)V",
            at = @At("HEAD"),
            argsOnly = true)
    private static int ftbevo$cheaperUpkeep(int cost, RitualHelper.RitualContext context) {
        return HemomancyHooks.ritualUpkeep(cost, context);
    }
}
