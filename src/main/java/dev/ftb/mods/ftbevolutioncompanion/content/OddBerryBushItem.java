package dev.ftb.mods.ftbevolutioncompanion.content;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class OddBerryBushItem extends BlockItem {
    public OddBerryBushItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);

        tooltip.accept(Component.translatable("block.ftbevolutioncompanion.odd_berry_bush.tooltip.harvest").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("block.ftbevolutioncompanion.odd_berry_bush.tooltip.shears").withStyle(ChatFormatting.GRAY));
        tooltip.accept(Component.translatable("block.ftbevolutioncompanion.odd_berry_bush.tooltip.spread").withStyle(ChatFormatting.GRAY));
    }
}
