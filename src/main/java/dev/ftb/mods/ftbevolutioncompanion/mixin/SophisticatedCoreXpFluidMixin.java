package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.XpFluidUnifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {
        "net.p3pp3rf1y.sophisticatedcore.upgrades.xppump.XpPumpUpgradeWrapper",
        "net.p3pp3rf1y.sophisticatedcore.upgrades.cooking.AutoCookingUpgradeWrapper",
        "net.p3pp3rf1y.sophisticatedcore.upgrades.magnet.MagnetUpgradeWrapper",
        "net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper"
}, remap = false)
public abstract class SophisticatedCoreXpFluidMixin {
    @ModifyExpressionValue(
            method = "*",
            at = @At(value = "INVOKE",
                    target = "Ljava/util/function/Supplier;get()Ljava/lang/Object;"))
    private static Object ftbevo$unifyXpFluid(Object original) {
        return XpFluidUnifier.substitute(original);
    }
}
