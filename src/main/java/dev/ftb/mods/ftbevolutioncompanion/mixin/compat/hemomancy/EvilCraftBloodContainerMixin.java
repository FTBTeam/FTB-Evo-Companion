package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.cyclops.evilcraft.core.item.ItemBloodContainer;
import org.cyclops.evilcraft.item.ItemMace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemBloodContainer.class, remap = false)
public abstract class EvilCraftBloodContainerMixin {
    @ModifyVariable(
            method = "consume(ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/neoforged/neoforge/fluids/FluidStack;",
            at = @At("HEAD"),
            argsOnly = true)
    private int ftbevo$consumeLess(int value, int amount, ItemStack itemStack, Player player) {
        if ((Object) this instanceof ItemMace) {
            return value;
        }
        return HemomancyHooks.bloodConsume(value, player);
    }

    @ModifyVariable(
            method = "canConsume(ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Z",
            at = @At("HEAD"),
            argsOnly = true)
    private int ftbevo$checkLess(int value, int amount, ItemStack itemStack, Player player) {
        return HemomancyHooks.bloodCheck(value, player);
    }
}
