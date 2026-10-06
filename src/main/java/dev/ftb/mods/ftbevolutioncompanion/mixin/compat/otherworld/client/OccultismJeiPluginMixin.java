package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld.client;

import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.klikli_dev.occultism.integration.jei.impl.JeiPlugin", remap = false)
public abstract class OccultismJeiPluginMixin {
    @ModifyArg(
            method = "registerRecipes",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lmezz/jei/api/registration/IRecipeRegistration;addRecipes(Lmezz/jei/api/recipe/types/IRecipeType;Ljava/util/List;)V"),
            index = 1)
    private List<?> ftbevo$nullToEmpty(List<?> recipes) {
        return recipes == null ? List.of() : recipes;
    }
}
