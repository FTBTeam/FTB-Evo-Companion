package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import dev.sterner.witchery.core.api.IAbility;
import dev.sterner.witchery.feature.ability.AbilityCooldownManager;

import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbilityCooldownManager.class, remap = false)
public abstract class WitcheryAbilityCooldownMixin {
    @ModifyExpressionValue(
            method = "startCooldown(Lnet/minecraft/world/entity/player/Player;Ldev/sterner/witchery/core/api/IAbility;)V",
            at = @At(value = "INVOKE", target = "Ldev/sterner/witchery/core/api/IAbility;getCooldown()I"))
    private int ftbevo$shorterCooldown(int cooldown, Player player, IAbility ability) {
        return HemomancyHooks.abilityCooldown(cooldown, player);
    }
}
