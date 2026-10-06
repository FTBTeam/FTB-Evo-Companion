package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client.SorceryClientDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.leclowndu93150.thaumaturge.compat.jei.category.InfusionCategory", remap = false)
public abstract class ThaumJeiInstabilityMixin {
    @ModifyExpressionValue(
            method =
                    "draw(Lnet/minecraft/world/item/crafting/RecipeHolder;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;"
                            + "Lnet/minecraft/client/gui/GuiGraphicsExtractor;DD)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lcom/leclowndu93150/thaumaturge/api/recipe/IInfusionRecipe;instability()I"))
    private int ftbevo$showStabilizedInstability(int instability) {
        return SorceryClientDisplay.thaumInstability(instability);
    }
}
