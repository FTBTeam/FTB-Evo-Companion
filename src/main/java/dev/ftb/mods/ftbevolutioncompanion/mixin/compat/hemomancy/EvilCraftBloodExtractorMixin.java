package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;

import net.minecraft.world.entity.player.Player;

import org.cyclops.evilcraft.item.ItemBloodExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemBloodExtractor.class, remap = false)
public abstract class EvilCraftBloodExtractorMixin {
    @ModifyVariable(
            method = "fillForAllBloodExtractors(Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0)
    private static int ftbevo$moreMinimumBlood(int value, Player player) {
        return HemomancyHooks.bloodHarvest(value, player);
    }

    @ModifyVariable(
            method = "fillForAllBloodExtractors(Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 1)
    private static int ftbevo$moreMaximumBlood(int value, Player player) {
        return HemomancyHooks.bloodHarvest(value, player);
    }
}
