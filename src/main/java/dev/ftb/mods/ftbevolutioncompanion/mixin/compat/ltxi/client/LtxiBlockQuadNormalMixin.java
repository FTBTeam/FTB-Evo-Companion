package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.core.Direction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "liedge.ltxindustries.client.LTXIRenderer", remap = false)
public abstract class LtxiBlockQuadNormalMixin {
    @WrapOperation(
            method =
                    "submitBlockFormatQuad(Lcom/mojang/blaze3d/vertex/PoseStack$Pose;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/core/Direction;FFFFFFFFFFII)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/mojang/blaze3d/vertex/VertexConsumer;setLight(I)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer ftbevo$addFaceNormal(
            VertexConsumer vertex,
            int packedLight,
            Operation<VertexConsumer> original,
            @Local(argsOnly = true) PoseStack.Pose pose,
            @Local(argsOnly = true) Direction side) {
        return original.call(vertex, packedLight).setNormal(pose, side.getStepX(), side.getStepY(), side.getStepZ());
    }
}
