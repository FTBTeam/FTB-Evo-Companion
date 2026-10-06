package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.ToolTraits;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.titammods.hephaestus_tools.tools.nbt.ToolStack", remap = false)
public abstract class HephaestusToolRecalculateMixin {
    @Inject(method = "recalculate(Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private static void ftbevo$applyTraitComponents(ItemStack stack, CallbackInfo ci) {
        ToolTraits.onRecalculate(stack);
    }
}
