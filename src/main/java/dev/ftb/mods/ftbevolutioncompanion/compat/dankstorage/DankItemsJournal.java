package dev.ftb.mods.ftbevolutioncompanion.compat.dankstorage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;

import java.util.ArrayList;
import java.util.List;

public final class DankItemsJournal extends SnapshotJournal<List<ItemStack>> {
    private final DankInventoryAccess inventory;

    public DankItemsJournal(DankInventoryAccess inventory) {
        this.inventory = inventory;
    }

    @Override
    protected List<ItemStack> createSnapshot() {
        NonNullList<ItemStack> items = inventory.ftbevo$items();
        List<ItemStack> copy = new ArrayList<>(items.size());
        for (ItemStack stack : items) {
            copy.add(stack.copy());
        }
        return copy;
    }

    @Override
    protected void revertToSnapshot(List<ItemStack> snapshot) {
        NonNullList<ItemStack> items = inventory.ftbevo$items();
        int size = Math.min(items.size(), snapshot.size());
        for (int i = 0; i < size; i++) {
            items.set(i, snapshot.get(i));
        }
        inventory.ftbevo$save();
    }
}
