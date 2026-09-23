package dev.ftb.mods.ftbevolutioncompanion.compat.dankstorage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface DankInventoryAccess {
    NonNullList<ItemStack> ftbevo$items();

    void ftbevo$save();

    DankItemsJournal ftbevo$journal();
}
