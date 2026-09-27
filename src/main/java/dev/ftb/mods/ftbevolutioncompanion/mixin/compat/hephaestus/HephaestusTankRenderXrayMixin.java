package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.titammods.common.blockentities.render.SearedTankRenderer", remap = false)
public abstract class HephaestusTankRenderXrayMixin {
    @ModifyExpressionValue(
            method = "submit(Lcom/titammods/common/blockentities/render/SearedTankRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;translucentMovingBlock()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType ftbevo$useCutoutForTankFluid(RenderType original) {
        return RenderTypes.cutoutMovingBlock();
    }
}
