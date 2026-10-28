package dev.ftb.mods.ftbevolutioncompanion.compat.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

public final class CurioSlotStacks {
    private CurioSlotStacks() {}

    public static int slotCount(Player player) {
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventoryOrNull(player);
        if (inventory == null) {
            return 0;
        }
        int count = 0;
        for (ICurioStacksHandler handler : inventory.getCurios().values()) {
            count += handler.getStacks().getSlots();
        }
        return count;
    }

    @Nullable
    public static ItemStack liveStack(Player player, int slot) {
        ICuriosItemHandler inventory = CuriosApi.getCuriosInventoryOrNull(player);
        if (inventory == null || slot < 0) {
            return null;
        }
        int remaining = slot;
        for (ICurioStacksHandler handler : inventory.getCurios().values()) {
            IDynamicStackHandler stacks = handler.getStacks();
            if (remaining < stacks.getSlots()) {
                return stacks.getStackInSlot(remaining);
            }
            remaining -= stacks.getSlots();
        }
        return null;
    }
}
