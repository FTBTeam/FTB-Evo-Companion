package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public final class FabricatorMenu extends AbstractContainerMenu {
    public static final int MACHINE_SLOTS = 12;
    private final FabricatorBlockEntity machine;

    public FabricatorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        this(id, inventory, requireMachine(inventory, buffer));
    }

    private static FabricatorBlockEntity requireMachine(Inventory inventory, RegistryFriendlyByteBuf buffer) {
        if (inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof FabricatorBlockEntity machine) return machine;
        throw new IllegalStateException("Fabricator menu opened without a Fabricator");
    }

    public FabricatorMenu(int id, Inventory inventory, FabricatorBlockEntity machine) {
        super(FabricatorRegistry.MENU.get(), id);
        this.machine = machine;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new ResourceHandlerSlot(machine.items(), machine.items()::set, row * 3 + column, 30 + column * 18, 34 + row * 18));
            }
        }
        for (int i = 0; i < 3; i++) {
            addSlot(new ResourceHandlerSlot(machine.items(), machine.items()::set, 9 + i, 140 + i * 18, 52) {
                @Override public boolean mayPlace(ItemStack stack) { return false; }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, 9 + row * 9 + column, 30 + column * 18, 132 + row * 18));
        }
        for (int column = 0; column < 9; column++) addSlot(new Slot(inventory, column, 30 + column * 18, 190));
    }

    public FabricatorBlockEntity machine() { return machine; }
    @Override public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(machine.getLevel(), machine.getBlockPos()), player, FabricatorRegistry.BLOCK.get());
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(stack, 0, FabricatorBlockEntity.INPUTS, false)) return ItemStack.EMPTY;
        slot.setByPlayer(stack.isEmpty() ? ItemStack.EMPTY : stack);
        slot.onTake(player, stack);
        return original;
    }
}
