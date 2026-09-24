package dev.ftb.mods.ftbevolutioncompanion.compat.iris;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import dev.ftb.mods.ftbevolutioncompanion.mixin.AutoGlowingGeoLayerAccessor;

import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.irisshaders.iris.api.v0.IrisShadowProgram;

public final class IrisGeckoGlow {
    private IrisGeckoGlow() {}

    public static void register() {
        RenderPipeline pipeline = AutoGlowingGeoLayerAccessor.ftbevo$getRenderPipeline();
        IrisApi.getInstance().assignPipeline(pipeline, IrisProgram.EMISSIVE_ENTITIES);
        IrisApi.getInstance().assignPipelineShadow(pipeline, IrisShadowProgram.SHADOW_ENTITIES);
    }
}
