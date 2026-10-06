package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.ToolTraits;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.titammods.hephaestus_tools.tools.item.ModifiableItem", remap = false)
public abstract class HephaestusTraitTooltipMixin {
    @Inject(
            method = "appendTrait(Lnet/minecraft/world/item/ItemStack;Ljava/util/function/Consumer;)V",
            at = @At("TAIL"))
    private static void ftbevo$appendCompanionTraits(ItemStack stack, Consumer<Component> tooltip, CallbackInfo ci) {
        ToolTraits.appendTooltip(stack, tooltip);
    }
}
