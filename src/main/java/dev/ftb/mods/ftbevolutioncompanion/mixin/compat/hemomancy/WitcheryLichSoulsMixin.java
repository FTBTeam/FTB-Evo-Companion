package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import dev.sterner.witchery_forbidden_magic.feature.affliction.lich.LichdomSoulPoolHandler;

import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LichdomSoulPoolHandler.class, remap = false)
public abstract class WitcheryLichSoulsMixin {
    @ModifyReturnValue(
            method = "getMaxSouls(Lnet/minecraft/server/level/ServerPlayer;)I",
            at = @At("RETURN"))
    private int ftbevo$moreSouls(int original, ServerPlayer player) {
        return HemomancyHooks.lichSouls(original, player);
    }

    @ModifyExpressionValue(
            method = "onPendantUnequipped(Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/sterner/witchery_forbidden_magic/feature/affliction/lich/LichdomSoulPoolHandler;getMaxSouls(I)I"))
    private int ftbevo$keepSkillSouls(int original, ServerPlayer player) {
        return HemomancyHooks.lichSouls(original, player);
    }
}
