package dev.ftb.mods.ftbevolutioncompanion.simplywinged;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ParagliderWings {
    public static final TagKey<Item> PARAGLIDERS = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("paraglider", "paragliders"));

    private ParagliderWings() {
    }

    public static boolean isWing(ItemStack stack) {
        return !stack.isEmpty() && stack.is(PARAGLIDERS);
    }

    public static boolean swapElytraCheck(ItemStack stack, Object item, Operation<Boolean> original) {
        if (item == Items.ELYTRA) {
            return isWing(stack);
        }
        return original.call(stack, item);
    }
}
