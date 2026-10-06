package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sgeconomy.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.sgeconomy.client.CoinBagLayer;
import net.neoforged.neoforge.client.event.ScreenEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.sirgrantd.sg_economy.internal.gui.CurrencyDisplay", remap = false)
public abstract class SGEconomyCurrencyDisplayMixin {
    @Inject(method = "eventHandler", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$drawBeforeTooltips(ScreenEvent.Render.Post event, CallbackInfo ci) {
        if (!CoinBagLayer.isDrawing()) {
            ci.cancel();
        }
    }
}
