package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;

import dev.sterner.witchery.content.block.cauldron.CauldronBlockEntity;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CauldronBlockEntity.class, remap = false)
public abstract class WitcheryCauldronMixin {
    @ModifyExpressionValue(
            method = "handleGlassBottleInteraction(Lnet/minecraft/world/entity/player/Player;"
                    + "Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Ljava/lang/Number;floatValue()F"))
    private float ftbevo$extraBottleChance(float chance, @Local(argsOnly = true) Player player) {
        return CovenMagic.extraBottleChance(player, chance);
    }

    @Inject(
            method = "handleGlassBottleInteraction(Lnet/minecraft/world/entity/player/Player;"
                    + "Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/InteractionResult;",
            at = @At("RETURN"))
    private void ftbevo$countBottledBrew(Player player, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir) {
        if (cir.getReturnValue() instanceof InteractionResult.Success) {
            CovenMagic.awardBottle(player);
        }
    }
}
