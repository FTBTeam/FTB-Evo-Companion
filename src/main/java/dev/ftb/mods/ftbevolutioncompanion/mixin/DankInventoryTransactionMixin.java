package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.compat.dankstorage.DankInventoryAccess;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "tfar.dankstorage.world.DankInventoryForge", remap = false)
public abstract class DankInventoryTransactionMixin {
    @Inject(
            method = "insert(ILnet/neoforged/neoforge/transfer/item/ItemResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"))
    private void ftbevo$snapshotBeforeInsert(int slot, ItemResource resource, int amount, TransactionContext transaction, CallbackInfoReturnable<Integer> cir) {
        if (amount > 0 && !resource.isEmpty()) {
            ((DankInventoryAccess) (Object) this).ftbevo$journal().updateSnapshots(transaction);
        }
    }

    @Inject(
            method = "extract(ILnet/neoforged/neoforge/transfer/item/ItemResource;ILnet/neoforged/neoforge/transfer/transaction/TransactionContext;)I",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$guardExtract(int slot, ItemResource resource, int amount, TransactionContext transaction, CallbackInfoReturnable<Integer> cir) {
        DankInventoryAccess inventory = (DankInventoryAccess) (Object) this;
        NonNullList<ItemStack> items = inventory.ftbevo$items();
        if (amount <= 0 || slot < 0 || slot >= items.size()) return;
        ItemStack existing = items.get(slot);
        if (existing.isEmpty()) return;
        if (!resource.isEmpty() && !resource.matches(existing)) {
            cir.setReturnValue(0);
            return;
        }
        inventory.ftbevo$journal().updateSnapshots(transaction);
    }
}
