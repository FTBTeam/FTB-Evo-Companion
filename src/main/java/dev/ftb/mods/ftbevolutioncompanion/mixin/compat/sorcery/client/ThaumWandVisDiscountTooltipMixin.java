package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.client.SorceryClientDisplay;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.leclowndu93150.thaumaturge.content.wands.ItemWand", remap = false)
public abstract class ThaumWandVisDiscountTooltipMixin {
    @ModifyArg(
            method =
                    "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;"
                            + "Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;"
                            + "Lnet/minecraft/world/item/TooltipFlag;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/leclowndu93150/thaumaturge/content/wands/WandVisHelper;getConsumptionModifier("
                                            + "Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;"
                                            + "Lnet/minecraft/resources/ResourceKey;Z)F"),
            index = 1)
    private Player ftbevo$applyVisDiscount(Player player) {
        return SorceryClientDisplay.thaumVisPayer(player);
    }
}
