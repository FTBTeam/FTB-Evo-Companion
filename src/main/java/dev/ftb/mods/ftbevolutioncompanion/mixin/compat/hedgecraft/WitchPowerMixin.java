package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;
import dev.sterner.witchery.core.util.WitcheryPowerHelper;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = WitcheryPowerHelper.class, remap = false)
public abstract class WitchPowerMixin {
    @ModifyReturnValue(method = "calculateWitchPower(Lnet/minecraft/world/entity/player/Player;)I", at = @At("RETURN"))
    private int ftbevo$addWitchPower(int power, @Local(argsOnly = true) Player player) {
        return CovenMagic.witchPower(player, power);
    }
}
