package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;

import dev.sterner.witchery.content.item.poppet.PoppetItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PoppetItem.class, remap = false)
public abstract class WitcheryPoppetMixin {
    @Inject(method = "damagePoppet(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;)Z",
            at = @At("HEAD"), cancellable = true)
    private void ftbevo$sparePoppet(ItemStack stack, LivingEntity owner, CallbackInfoReturnable<Boolean> cir) {
        if (CovenMagic.sparePoppet(owner)) {
            cir.setReturnValue(false);
        }
    }
}
