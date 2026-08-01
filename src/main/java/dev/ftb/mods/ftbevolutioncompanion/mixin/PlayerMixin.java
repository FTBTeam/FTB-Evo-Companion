package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsRegistry;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "getDimensionChangingDelay", at = @At("HEAD"), cancellable = true)
    private void ftbevo$instantPortalArrivalDelay(CallbackInfoReturnable<Integer> cir) {
        Player player = (Player) (Object) this;
        if (player.getAttributeValue(AthleticsRegistry.INSTANT_PORTALS) > 0) {
            cir.setReturnValue(40);
        }
    }
}
