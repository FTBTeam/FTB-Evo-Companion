package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AutoGlowingGeoLayer.class)
public interface AutoGlowingGeoLayerAccessor {
    @Accessor("RENDER_PIPELINE")
    static RenderPipeline ftbevo$getRenderPipeline() {
        throw new AssertionError();
    }
}
