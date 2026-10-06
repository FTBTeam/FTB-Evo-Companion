package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.compat.mysticalagriculture.MachineClientLevel;
import java.util.function.Supplier;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(
        targets = {
            "com.blakebr0.mysticalagriculture.tileentity.SouliumSpawnerTileEntity",
            "com.blakebr0.mysticalagriculture.tileentity.HarvesterTileEntity",
            "com.blakebr0.mysticalagriculture.tileentity.EssenceFurnaceTileEntity",
            "com.blakebr0.mysticalagriculture.tileentity.ReprocessorTileEntity",
            "com.blakebr0.mysticalagriculture.tileentity.SoulExtractorTileEntity"
        },
        remap = false)
public abstract class MysticalAgricultureMachineClientLevelMixin {
    @ModifyArg(
            method = "createInventoryHandler()Lcom/blakebr0/cucumber/inventory/CItemStacksHandler;",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "createInventoryHandler(Lcom/blakebr0/cucumber/inventory/OnContentsChangedFunction;Ljava/util/function/Supplier;)Lcom/blakebr0/cucumber/inventory/CItemStacksHandler;"),
            index = 1,
            require = 1)
    private static Supplier<Level> ftbevo$clientLevel(Supplier<Level> level) {
        return MachineClientLevel.supplier();
    }
}
