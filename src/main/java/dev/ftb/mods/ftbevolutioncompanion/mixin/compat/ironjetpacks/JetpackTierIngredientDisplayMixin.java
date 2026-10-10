package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ironjetpacks;

import com.blakebr0.ironjetpacks.crafting.ingredient.JetpackTierIngredient;
import java.util.Arrays;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = JetpackTierIngredient.class, remap = false)
public abstract class JetpackTierIngredientDisplayMixin {
    @Shadow
    private ItemStack[] stacks;

    @Shadow
    private void initMatchingStacks() {}

    public SlotDisplay display() {
        if (this.stacks == null) {
            this.initMatchingStacks();
        }
        return new SlotDisplay.Composite(Arrays.stream(this.stacks)
                .filter(stack -> !stack.isEmpty())
                .<SlotDisplay>map(
                        stack -> new SlotDisplay.ItemStackSlotDisplay(ItemStackTemplate.fromNonEmptyStack(stack)))
                .toList());
    }
}
