package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.compat.dankstorage.DankInventoryAccess;
import dev.ftb.mods.ftbevolutioncompanion.compat.dankstorage.DankItemsJournal;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "tfar.dankstorage.inventory.DankInventory", remap = false)
public abstract class DankInventoryAccessMixin implements DankInventoryAccess {
    @Shadow
    public NonNullList<ItemStack> items;

    @Unique
    private DankItemsJournal ftbevo$journal;

    @Shadow
    public abstract void setDirty(boolean needsSort);

    @Override
    public NonNullList<ItemStack> ftbevo$items() {
        return items;
    }

    @Override
    public void ftbevo$save() {
        setDirty(false);
    }

    @Override
    public DankItemsJournal ftbevo$journal() {
        if (ftbevo$journal == null) {
            ftbevo$journal = new DankItemsJournal(this);
        }
        return ftbevo$journal;
    }
}
