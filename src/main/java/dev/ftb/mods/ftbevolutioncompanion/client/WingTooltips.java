package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.simplywinged.ParagliderWings;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

public final class WingTooltips {
    private WingTooltips() {
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();

        if (!ParagliderWings.isWing(stack) || !stack.isValidRepairItem(new ItemStack(Items.LEATHER))) {
            return;
        }

        event.getToolTip().add(Component.translatable("ftbevolutioncompanion.wings.repair_with_leather")
                .withStyle(ChatFormatting.GRAY));
    }
}
