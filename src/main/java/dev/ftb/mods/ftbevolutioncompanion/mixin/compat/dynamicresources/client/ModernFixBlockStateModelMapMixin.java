package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.embeddedt.modernfix.dynresources.BlockStateModelMap", remap = false)
public abstract class ModernFixBlockStateModelMapMixin {
    @Shadow
    @Final
    private BlockStateModel fallbackModel;

    @ModifyReturnValue(
            method = "get(Ljava/lang/Object;)Lnet/minecraft/client/renderer/block/dispatch/BlockStateModel;",
            at = @At("RETURN"))
    private BlockStateModel ftbevo$fallbackInsteadOfNull(BlockStateModel model) {
        return model != null ? model : fallbackModel;
    }
}
