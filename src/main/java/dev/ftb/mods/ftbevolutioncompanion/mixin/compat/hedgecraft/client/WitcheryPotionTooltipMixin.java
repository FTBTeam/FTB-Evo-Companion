package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client.CovenMagicClient;
import dev.sterner.witchery.content.item.WitcheryPotionIngredient;
import dev.sterner.witchery.content.item.WitcheryPotionItem;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WitcheryPotionItem.class, remap = false)
public abstract class WitcheryPotionTooltipMixin {
    @ModifyExpressionValue(
            method = "addPotionTooltip(Ljava/util/function/Consumer;Ljava/util/List;F)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/sterner/witchery/content/item/WitcheryPotionItem$Companion;getMergedEffectModifier("
                                            + "Ljava/util/List;)Ldev/sterner/witchery/content/item/WitcheryPotionIngredient$EffectModifier;"))
    private WitcheryPotionIngredient.EffectModifier ftbevo$showSkillBrewPotency(
            WitcheryPotionIngredient.EffectModifier modifier,
            @Local(argsOnly = true) List<WitcheryPotionIngredient> ingredients) {
        return CovenMagicClient.brewModifier(ingredients, modifier);
    }
}
