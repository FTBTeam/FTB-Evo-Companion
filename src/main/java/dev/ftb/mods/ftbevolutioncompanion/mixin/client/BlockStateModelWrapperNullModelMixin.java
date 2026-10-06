package dev.ftb.mods.ftbevolutioncompanion.mixin.client;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.block.model.BlockStateModelWrapper;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4fc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockStateModelWrapper.class)
public abstract class BlockStateModelWrapperNullModelMixin {
    @Shadow
    @Final
    private BlockStateModel model;

    @Shadow
    @Final
    private Matrix4fc transformation;

    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void ftbevo$skipMissingModel(
            BlockModelRenderState output,
            BlockState blockState,
            BlockDisplayContext displayContext,
            long seed,
            CallbackInfo ci) {
        if (this.model == null) {
            output.setupModel(this.transformation, false);
            ci.cancel();
        }
    }
}
