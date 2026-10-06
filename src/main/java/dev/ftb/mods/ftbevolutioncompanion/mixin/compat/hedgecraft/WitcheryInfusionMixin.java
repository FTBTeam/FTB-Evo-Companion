package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.feature.infusion.InfusionHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = InfusionHandler.class, remap = false)
public abstract class WitcheryInfusionMixin {
    @ModifyVariable(
            method = "increaseInfusionCharge(Lnet/minecraft/world/entity/player/Player;I)V",
            at = @At("HEAD"),
            argsOnly = true)
    private static int ftbevo$scaleInfusionGain(int toAdd, @Local(argsOnly = true) Player player) {
        return CovenMagic.infusionGain(player, toAdd);
    }

    @ModifyVariable(
            method = "decreaseInfusionCharge(Lnet/minecraft/world/entity/player/Player;I)V",
            at = @At("HEAD"),
            argsOnly = true)
    private static int ftbevo$scaleInfusionSpend(int toRemove, @Local(argsOnly = true) Player player) {
        return CovenMagic.infusionSpend(player, toRemove);
    }
}
