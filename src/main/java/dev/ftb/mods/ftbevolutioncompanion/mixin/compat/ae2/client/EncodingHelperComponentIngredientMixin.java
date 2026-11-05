package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2.client;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "appeng.integration.modules.itemlists.EncodingHelper", remap = false)
public abstract class EncodingHelperComponentIngredientMixin {
    @WrapOperation(
            method = "encodeCraftingRecipe",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/core/NonNullList;set(ILjava/lang/Object;)Ljava/lang/Object;",
                            ordinal = 0))
    private static Object ftbevo$keepIngredientComponents(
            NonNullList<ItemStack> inputs,
            int slot,
            Object chosen,
            Operation<Object> original,
            @Local(argsOnly = true) List<List<GenericStack>> shown,
            @Local Ingredient ingredient) {
        if (chosen instanceof ItemStack stack && !stack.isEmpty() && !ingredient.test(stack)) {
            return original.call(inputs, slot, ftbevo$matching(ingredient, stack, shown));
        }
        return original.call(inputs, slot, chosen);
    }

    private static ItemStack ftbevo$matching(Ingredient ingredient, ItemStack chosen, List<List<GenericStack>> shown) {
        for (List<GenericStack> slotStacks : shown) {
            for (GenericStack generic : slotStacks) {
                if (generic.what() instanceof AEItemKey key) {
                    ItemStack stack = key.toStack();
                    if (ingredient.test(stack)) {
                        return stack;
                    }
                }
            }
        }
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            for (ItemStack stack : ingredient.display().resolveForStacks(SlotDisplayContext.fromLevel(level))) {
                if (!stack.isEmpty() && ingredient.test(stack)) {
                    return stack.copyWithCount(1);
                }
            }
        }
        return chosen;
    }
}
