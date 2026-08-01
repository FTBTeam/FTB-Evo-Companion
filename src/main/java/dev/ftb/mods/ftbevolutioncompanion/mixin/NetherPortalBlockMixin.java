package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsRegistry;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.NetherPortalBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
    @Inject(method = "getPortalTransitionTime", at = @At("HEAD"), cancellable = true)
    private void ftbevo$instantPortalTransition(ServerLevel level, Entity entity, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof Player player
                && player.getAttributeValue(AthleticsRegistry.INSTANT_PORTALS) > 0) {
            cir.setReturnValue(0);
        }
    }
}
