package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client.SorceryClientDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.leclowndu93150.thaumaturge.client.render.research.RecipeDisplayWidget", remap = false)
public abstract class ThaumResearchInstabilityMixin {
    @ModifyExpressionValue(
            method = "drawInfusionPage(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II"
                    + "Lcom/leclowndu93150/thaumaturge/content/infusion/InfusionRecipeDisplay;"
                    + "Lnet/minecraft/util/context/ContextMap;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/leclowndu93150/thaumaturge/content/infusion/InfusionRecipeDisplay;instability()I"))
    private static int ftbevo$showStabilizedInstability(int instability) {
        return SorceryClientDisplay.thaumInstability(instability);
    }
}
