package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.ThaumaturgeHooks;

import net.minecraft.server.level.ServerPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(targets = "com.leclowndu93150.thaumaturge.content.warp.WarpManager", remap = false)
public abstract class ThaumWarpWardMixin {
    @ModifyVariable(
            method = "addWarp(Lnet/minecraft/server/level/ServerPlayer;ILcom/leclowndu93150/thaumaturge/api/warp/WarpType;)V",
            at = @At("HEAD"), argsOnly = true)
    private static int ftbevo$wardWarp(int amount, @Local(argsOnly = true) ServerPlayer player) {
        return ThaumaturgeHooks.wardWarp(player, amount);
    }
}
