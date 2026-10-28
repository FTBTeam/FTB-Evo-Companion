package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightEngine.class)
public abstract class LightEngineMissingSectionGuardMixin {
    @Unique
    private static final int FTBEVO_SKIP_ENTRY_LEVEL = 16;

    @WrapOperation(
            method = "propagateIncreases()I",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/lighting/LayerLightSectionStorage;getStoredLevel(J)I"))
    private int ftbevo$skipMissingSection(
            LayerLightSectionStorage<?> storage, long blockNode, Operation<Integer> original) {
        if (!((LayerLightSectionStorageInvoker) storage)
                .ftbevo$storingLightForSection(SectionPos.blockToSection(blockNode))) {
            return FTBEVO_SKIP_ENTRY_LEVEL;
        }
        return original.call(storage, blockNode);
    }
}
