package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.breakinblocks.animusnv.util.AnimusRitualHelper;
import com.breakinblocks.neovitae.api.soul.IAnima;
import com.breakinblocks.neovitae.ritual.IMasterRitualStone;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AnimusRitualHelper.class, remap = false)
public abstract class AnimusRitualNetworkMixin {
    @ModifyReturnValue(
            method = "getOwnerNetwork(Lcom/breakinblocks/neovitae/ritual/IMasterRitualStone;)Lcom/breakinblocks/neovitae/api/soul/IAnima;",
            at = @At("RETURN"))
    private static IAnima ftbevo$discountedNetwork(IAnima original, IMasterRitualStone stone) {
        return HemomancyHooks.animusNetwork(original, stone);
    }
}
