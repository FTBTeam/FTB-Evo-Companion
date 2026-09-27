package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy.client;

import com.breakinblocks.neovitae.compat.jei.ritual.RitualRecipeCategory;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.client.HemomancyDisplay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.text.DecimalFormat;

@Mixin(value = RitualRecipeCategory.class, remap = false)
public abstract class NeoVitaeJeiRitualMixin {
    @ModifyExpressionValue(
            method = "draw(Lcom/breakinblocks/neovitae/compat/jei/ritual/RitualJEIRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphicsExtractor;DD)V",
            at = @At(value = "INVOKE",
                    target = "Lcom/breakinblocks/neovitae/compat/jei/ritual/RitualJEIRecipe;activationCost()I"))
    private int ftbevo$skillActivation(int cost) {
        return HemomancyDisplay.ritualActivation(cost);
    }

    @WrapOperation(
            method = "draw(Lcom/breakinblocks/neovitae/compat/jei/ritual/RitualJEIRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphicsExtractor;DD)V",
            at = @At(value = "INVOKE",
                    target = "Ljava/text/DecimalFormat;format(J)Ljava/lang/String;",
                    ordinal = 1))
    private String ftbevo$skillUpkeep(DecimalFormat format, long cost, Operation<String> original) {
        String reduced = HemomancyDisplay.ritualUpkeep(cost);
        return reduced != null ? reduced : original.call(format, cost);
    }
}
