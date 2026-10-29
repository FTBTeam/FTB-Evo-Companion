package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import liedge.ltxindustries.LTXIEventHandler;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LTXIEventHandler.class, remap = false)
public abstract class LTXIZeroDamageShieldMixin {
    @Inject(method = "onLivingIncomingDamageLowest", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$skipZeroDamage(LivingIncomingDamageEvent event, CallbackInfo ci) {
        if (event.getAmount() <= 0.0F) {
            ci.cancel();
        }
    }
}
