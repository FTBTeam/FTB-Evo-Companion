package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.XpFluidUnifier;

import net.neoforged.neoforge.registries.DeferredHolder;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = {
        "com.enderio.enderio.content.machines.obelisks.xp.XPObeliskBlockEntity",
        "com.enderio.enderio.content.machines.vacuum.xp.XPVacuumBlockEntity",
        "com.enderio.enderio.content.tools.ExperienceRodItem",
        "com.enderio.enderio.content.tools.vials.VoidVialItem",
        "com.enderio.enderio.content.machines.soul_binder.SoulBinderBlockEntity"
}, remap = false)
public abstract class EnderIOXpFluidMixin {
    @ModifyExpressionValue(
            method = "*",
            at = @At(value = "INVOKE",
                    target = "Lcom/enderio/core/common/registries/FluidDeferredHolders;"
                            + "source()Lnet/neoforged/neoforge/registries/DeferredHolder;"))
    private static DeferredHolder<?, ?> ftbevo$unifyXpFluid(DeferredHolder<?, ?> original) {
        return XpFluidUnifier.substituteHolder(original);
    }
}
