package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.xycraft;

import net.neoforged.neoforge.transfer.item.ItemResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "tv.soaryn.xycraft.machines.content.multiblock.tank.TankMultiBlock", remap = false)
public abstract class XyCraftTankEmptyResourceMixin {
    @Inject(
            method = "isValid(ILnet/neoforged/neoforge/transfer/item/ItemResource;)Z",
            at = @At("HEAD"),
            cancellable = true)
    private static void ftbevolutioncompanion$rejectEmptyResource(
            int slot, ItemResource resource, CallbackInfoReturnable<Boolean> cir) {
        // Inventory integrations can probe an empty slot before a player inserts anything.
        if (resource.isEmpty()) {
            cir.setReturnValue(false);
        }
    }
}
