package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.breakinblocks.neovitae.common.blockentity.MasterRitualStoneBlockEntity;
import com.breakinblocks.neovitae.ritual.Ritual;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MasterRitualStoneBlockEntity.class, remap = false)
public abstract class NeoVitaeRitualActivationMixin {
    @ModifyExpressionValue(
            method = "activateRitual(Lcom/breakinblocks/neovitae/ritual/Ritual;Lnet/minecraft/world/entity/player/Player;I)Z",
            at = @At(value = "INVOKE", target = "Lcom/breakinblocks/neovitae/ritual/Ritual;getActivationCost()I"))
    private int ftbevo$cheaperActivation(int cost, Ritual ritual, Player player) {
        return HemomancyHooks.ritualActivation(cost, player);
    }
}
