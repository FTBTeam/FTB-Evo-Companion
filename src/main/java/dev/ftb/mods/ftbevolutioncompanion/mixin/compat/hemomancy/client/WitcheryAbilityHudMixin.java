package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.client.HemomancyDisplay;
import dev.sterner.witchery.core.api.IAbility;
import dev.sterner.witchery.feature.ability.AbilityHudRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AbilityHudRenderer.class, remap = false)
public abstract class WitcheryAbilityHudMixin {
    @WrapOperation(
            method =
                    "drawAbilityBar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;II)V",
            at = @At(value = "INVOKE", target = "Ldev/sterner/witchery/core/api/IAbility;getCooldown()I"))
    private int ftbevo$skillCooldown(
            IAbility ability, Operation<Integer> original, GuiGraphicsExtractor graphics, Player player) {
        return HemomancyDisplay.abilityCooldown(original.call(ability), ability, player);
    }
}
