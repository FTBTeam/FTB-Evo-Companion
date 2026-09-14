package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "org.exodusstudio.stellaris.common.blocks.entities.machines.PumpjackBlockEntity", remap = false)
public abstract class StellarisPumpjackOilMixin {
    private static final Identifier ftbevo$ORITECH_OIL =
            Identifier.fromNamespaceAndPath("oritech", "still_oil");

    private static Fluid ftbevo$oil;

    @ModifyArg(
            method = "tick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;)V",
            at = @At(value = "INVOKE",
                    target = "Ldev/architectury/fluid/FluidStack;create("
                            + "Lnet/minecraft/world/level/material/Fluid;J)"
                            + "Ldev/architectury/fluid/FluidStack;"),
            index = 0)
    private Fluid ftbevo$pumpOritechOil(Fluid original) {
        if (ftbevo$oil == null) {
            Fluid found = BuiltInRegistries.FLUID.getValue(ftbevo$ORITECH_OIL);
            ftbevo$oil = found == Fluids.EMPTY ? original : found;
        }
        return ftbevo$oil;
    }
}
