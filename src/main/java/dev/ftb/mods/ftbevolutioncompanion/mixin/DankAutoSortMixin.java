package dev.ftb.mods.ftbevolutioncompanion.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "tfar.dankstorage.inventory.DankInventory", remap = false)
public abstract class DankAutoSortMixin {
    @Unique
    private boolean ftbevo$serverInventory;

    @Shadow
    public abstract void sort();

    @Inject(method = "<init>(IILtfar/dankstorage/world/DankSavedData;)V", at = @At("RETURN"))
    private void ftbevo$identifyServerInventory(int slots, int capacity, @Coerce Object savedData, CallbackInfo ci) {
        ftbevo$serverInventory = savedData != null;
    }

    @Redirect(
            method = "setDirty(Z)V",
            at = @At(value = "INVOKE", target = "Ltfar/dankstorage/inventory/DankInventory;sort()V"))
    private void ftbevo$sortOnlyOnServer(@Coerce Object inventory) {
        // Client dummy inventories must apply the server's slot updates without merging or reordering them.
        if (ftbevo$serverInventory) {
            sort();
        }
    }
}
