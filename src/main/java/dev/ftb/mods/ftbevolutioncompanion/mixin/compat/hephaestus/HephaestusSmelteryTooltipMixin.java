package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.HephaestusFluidUnits;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.network.chat.MutableComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.titammods.client.screen.SmelteryScreen", remap = false)
public abstract class HephaestusSmelteryTooltipMixin {
    @ModifyReturnValue(method = "fluidBreakdown(I)Lnet/minecraft/network/chat/MutableComponent;", at = @At("RETURN"))
    private MutableComponent ftbevo$fixFluidBreakdown(MutableComponent original, int amount) {
        return HephaestusFluidUnits.breakdown(amount);
    }
}
