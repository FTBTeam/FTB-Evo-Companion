package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client.CovenMagicClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "dev.sterner.witchery.integration.modonomicon.ritual.BookRitualRecipePageRenderer", remap = false)
public abstract class WitcheryRitualBookMixin {
    @ModifyExpressionValue(
            method = "drawRecipe(Lnet/minecraft/client/gui/GuiGraphicsExtractor;"
                    + "Lnet/minecraft/world/item/crafting/display/RecipeDisplayEntry;IIIIZ)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/sterner/witchery/content/recipe/ritual/RitualRecipeDisplay;getAltarPower()I"))
    private int ftbevo$showRiteCost(int power) {
        return CovenMagicClient.riteAltarPower(power);
    }

    @ModifyExpressionValue(
            method = "drawRecipe(Lnet/minecraft/client/gui/GuiGraphicsExtractor;"
                    + "Lnet/minecraft/world/item/crafting/display/RecipeDisplayEntry;IIIIZ)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/sterner/witchery/content/recipe/ritual/RitualRecipeDisplay;getAltarPowerPerSecond()I"))
    private int ftbevo$showRiteUpkeep(int power) {
        return CovenMagicClient.riteAltarPower(power);
    }
}
