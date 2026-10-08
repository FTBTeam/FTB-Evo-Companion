package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.xycraft.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.iris.IrisXyCraftLasers;
import net.minecraft.client.renderer.LevelRenderer;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererXyCraftLaserMixin {
    @Unique
    private static final boolean ftbevo$IRIS = ModList.get().isLoaded("iris");

    @Inject(
            method =
                    "renderLevel(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;ZLnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/state/level/LevelRenderState;reset()V"))
    private void ftbevo$blitXyCraftLasersAfterIris(CallbackInfo ci) {
        if (ftbevo$IRIS) {
            IrisXyCraftLasers.blitAfterShaderpack();
        }
    }
}
