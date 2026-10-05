package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.logging.RecipePlacementLog;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.crafting.RecipeManager;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerPlacementLogMixin {
    @WrapOperation(
            method = "lambda$finalizeRecipeLoading$1",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private static void ftbevo$countUnplaceable(Logger logger, String format, Object recipeId, Operation<Void> original) {
        RecipePlacementLog.unplaceable(recipeId);
    }

    @Inject(method = "finalizeRecipeLoading", at = @At("TAIL"))
    private void ftbevo$logUnplaceableSummary(FeatureFlagSet enabledFlags, CallbackInfo ci) {
        RecipePlacementLog.summary();
    }
}
