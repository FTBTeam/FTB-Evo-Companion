package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hemomancy;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.hemomancy.HemomancyHooks;
import dev.sterner.witchery_forbidden_magic.feature.necromancy.NecroHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = NecroHandler.class, remap = false)
public abstract class WitcheryNecromancyMixin {
    @ModifyVariable(
            method =
                    "summonNecroAroundPos(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;I)V",
            at = @At("HEAD"),
            argsOnly = true)
    private int ftbevo$widerRadius(int radius, ServerLevel level, Player summoner) {
        return HemomancyHooks.necroRadius(radius, summoner);
    }

    @ModifyExpressionValue(
            method =
                    "summonNecroAroundPos(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;I)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ldev/sterner/witchery_forbidden_magic/feature/necromancy/NecroHandler;despawnTimeForLevel(I)J"))
    private long ftbevo$longerLifetime(long ticks, ServerLevel level, Player summoner) {
        return HemomancyHooks.necroLifetime(ticks, summoner);
    }
}
