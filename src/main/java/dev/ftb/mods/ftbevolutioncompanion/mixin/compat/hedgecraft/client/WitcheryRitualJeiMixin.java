package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client.CovenMagicClient;

import dev.sterner.witchery.integration.jei.RitualRecipeCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RitualRecipeCategory.class, remap = false)
public abstract class WitcheryRitualJeiMixin {
    @ModifyExpressionValue(
            method = "draw(Ldev/sterner/witchery/integration/jei/wrapper/RitualJeiRecipe;"
                    + "Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphicsExtractor;DD)V",
            at = @At(value = "INVOKE", target = "Ldev/sterner/witchery/content/recipe/ritual/RitualRecipe;getAltarPower()I"))
    private int ftbevo$showRiteCost(int power) {
        return CovenMagicClient.riteAltarPower(power);
    }

    @ModifyExpressionValue(
            method = "draw(Ldev/sterner/witchery/integration/jei/wrapper/RitualJeiRecipe;"
                    + "Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphicsExtractor;DD)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/sterner/witchery/content/recipe/ritual/RitualRecipe;getAltarPowerPerSecond()I"))
    private int ftbevo$showRiteUpkeep(int power) {
        return CovenMagicClient.riteAltarPower(power);
    }
}
