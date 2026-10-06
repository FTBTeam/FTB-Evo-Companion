package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.content.item.WitcheryPotionIngredient;
import dev.sterner.witchery.content.item.WitcheryPotionItem;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WitcheryPotionItem.class, remap = false)
public abstract class WitcheryPotionItemMixin {
    @ModifyExpressionValue(
            method = "finishUsingItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;"
                    + "Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/item/ItemStack;",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/sterner/witchery/content/item/WitcheryPotionItem$Companion;getMergedEffectModifier("
                                            + "Ljava/util/List;)Ldev/sterner/witchery/content/item/WitcheryPotionIngredient$EffectModifier;"))
    private WitcheryPotionIngredient.EffectModifier ftbevo$boostDrunkBrew(
            WitcheryPotionIngredient.EffectModifier modifier, @Local(argsOnly = true) LivingEntity drinker) {
        return CovenMagic.brewModifier(drinker, modifier);
    }
}
