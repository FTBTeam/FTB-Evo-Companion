package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.item.crafting.RecipeHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "cy.jdkdigital.productivebees.common.block.entity.BreedingChamberBlockEntity", remap = false)
public abstract class ProductiveBeesBreedingRerollMixin {
    @Shadow
    public RecipeHolder<?> chosenRecipe;

    @Inject(method = "completeBreeding", at = @At("RETURN"))
    private void ftbevo$rerollNextOffspring(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ()) {
            this.chosenRecipe = null;
        }
    }
}
