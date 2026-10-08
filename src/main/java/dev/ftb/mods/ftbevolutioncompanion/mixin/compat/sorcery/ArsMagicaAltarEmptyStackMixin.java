package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "at.minecraftschurli.mods.arsmagicalegacy.spell.ItemSpellIngredient", remap = false)
public abstract class ArsMagicaAltarEmptyStackMixin {
    @Inject(method = "consume(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void ftbevo$skipEmptyStack(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
