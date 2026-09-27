package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.neoforged.neoforge.fluids.FluidStack;

import org.cyclops.evilcraft.item.ItemMace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemMace.class, remap = false)
public abstract class EvilCraftMaceMixin {
    @WrapOperation(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lorg/cyclops/evilcraft/item/ItemMace;consume(ILnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;)Lnet/neoforged/neoforge/fluids/FluidStack;"))
    private FluidStack ftbevo$drainLess(ItemMace mace, int amount, ItemStack itemStack, Player player,
                                        Operation<FluidStack> original) {
        if (amount <= 2) {
            return original.call(mace, amount, itemStack, player);
        }
        int reduced = HemomancyHooks.bloodConsume(amount, player);
        if (reduced >= amount) {
            return original.call(mace, amount, itemStack, player);
        }
        FluidStack drained = original.call(mace, reduced, itemStack, player);
        if (drained == null || drained.isEmpty()) {
            return drained;
        }
        int got = drained.getAmount();
        if (got >= reduced) {
            return drained.copyWithAmount(amount);
        }
        double efficiency = HemomancyHooks.bloodEfficiency(player);
        return drained.copyWithAmount(Math.min(amount, (int) Math.round(got / (1.0 - efficiency))));
    }
}
