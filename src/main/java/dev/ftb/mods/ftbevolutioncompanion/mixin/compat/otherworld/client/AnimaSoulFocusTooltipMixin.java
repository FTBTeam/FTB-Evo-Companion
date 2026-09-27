package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld.client;

import dev.anima.SoulFocusItem;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.client.OtherworldTooltips;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = SoulFocusItem.class, remap = false)
public abstract class AnimaSoulFocusTooltipMixin {
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;"
            + "Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;"
            + "Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V",
                    ordinal = 0, shift = At.Shift.AFTER))
    private void ftbevo$showSoulThrift(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                       Consumer<Component> builder, TooltipFlag flag, CallbackInfo ci) {
        Component thrift = OtherworldTooltips.soulThrift();
        if (thrift != null) {
            builder.accept(thrift);
        }
    }
}
