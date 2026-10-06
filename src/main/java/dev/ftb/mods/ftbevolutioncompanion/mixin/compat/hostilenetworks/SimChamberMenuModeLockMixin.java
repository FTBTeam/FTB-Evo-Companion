package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hostilenetworks;

import dev.shadowsoffire.hostilenetworks.gui.SimChamberMenu;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SimChamberMenu.class, remap = false)
public abstract class SimChamberMenuModeLockMixin {
    @Inject(
            method = "clickMenuButton(Lnet/minecraft/world/entity/player/Player;I)Z",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$blockModeSwitch(Player player, int id, CallbackInfoReturnable<Boolean> cir) {
        if (id == 3 || id == 4) {
            cir.setReturnValue(false);
        }
    }
}
