package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.content.entity.WitcheryThrownPotion;
import dev.sterner.witchery.content.item.WitcheryPotionIngredient;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WitcheryThrownPotion.class, remap = false)
public abstract class WitcheryThrownPotionMixin {
    @ModifyExpressionValue(
            method = "applySplash(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/Entity;"
                    + "Lnet/minecraft/world/phys/HitResult;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/sterner/witchery/content/item/WitcheryPotionItem$Companion;getMergedEffectModifier("
                                            + "Ljava/util/List;)Ldev/sterner/witchery/content/item/WitcheryPotionIngredient$EffectModifier;"))
    private WitcheryPotionIngredient.EffectModifier ftbevo$boostSplashBrew(
            WitcheryPotionIngredient.EffectModifier modifier) {
        return CovenMagic.brewModifier(((Projectile) (Object) this).getOwner(), modifier);
    }
}
