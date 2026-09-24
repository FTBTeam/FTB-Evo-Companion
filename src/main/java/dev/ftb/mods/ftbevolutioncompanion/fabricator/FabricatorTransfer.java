package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/** Plans the complete inventory change before touching either inventory. */
public final class FabricatorTransfer {
    public record Plan(List<ItemStack> machine, List<ItemStack> player) {}
    private FabricatorTransfer() {}

    public static @Nullable Plan plan(FabricatorMenu menu, Inventory inventory, FabricatorRecipe recipe, boolean maximum) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 9; i++) inputs.add(menu.getSlot(i).getItem().copy());
        List<ItemStack> player = new ArrayList<>();
        for (int i = 0; i < 36; i++) player.add(inventory.getItem(i).copy());
        Plan best = null;
        for (int crafts = 1; crafts <= (maximum ? 64 : 1); crafts++) {
            List<ItemStack> combined = new ArrayList<>(inputs);
            combined.addAll(player);
            int count = crafts;
            int[] allocation = IngredientAllocation.allocate(combined.stream().mapToInt(ItemStack::getCount).toArray(),
                    recipe.ingredients().stream().mapToInt(input -> input.count() * count).toArray(),
                    (slot, requirement) -> recipe.ingredients().get(requirement).ingredient().test(combined.get(slot)));
            if (allocation == null) break;
            List<ItemStack> target = inputs.stream().map(ItemStack::copy).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            List<ItemStack> remaining = player.stream().map(ItemStack::copy).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            boolean fits = true;
            for (int slot = 0; slot < 36 && fits; slot++) {
                int amount = allocation[9 + slot];
                ItemStack stack = remaining.get(slot);
                int requested = amount;
                for (int pass = 0; pass < 2 && amount > 0; pass++) {
                    for (int targetSlot = 0; targetSlot < 9 && amount > 0; targetSlot++) {
                        ItemStack existing = target.get(targetSlot);
                        if (pass == 0 ? existing.isEmpty() || !ItemStack.isSameItemSameComponents(existing, stack) : !existing.isEmpty()) continue;
                        int moved = Math.min(amount, stack.getMaxStackSize() - existing.getCount());
                        if (moved <= 0) continue;
                        target.set(targetSlot, stack.copyWithCount(existing.getCount() + moved));
                        amount -= moved;
                    }
                }
                if (amount > 0) fits = false;
                else stack.shrink(requested);
            }
            if (!fits) break;
            best = new Plan(target, remaining);
        }
        return best;
    }

    public static void apply(FabricatorMenu menu, Inventory inventory, Plan plan) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = plan.machine.get(i);
            menu.machine().items().set(i, ItemResource.of(stack), stack.getCount());
        }
        for (int i = 0; i < 36; i++) inventory.setItem(i, plan.player.get(i));
        inventory.setChanged();
        menu.broadcastChanges();
    }
}
