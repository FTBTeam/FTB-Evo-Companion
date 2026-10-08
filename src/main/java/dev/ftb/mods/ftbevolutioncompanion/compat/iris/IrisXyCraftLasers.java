package dev.ftb.mods.ftbevolutioncompanion.compat.iris;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.irisshaders.iris.api.v0.IrisApi;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import tv.soaryn.xycraft.machines.client.render.laser.XynergyGraphRenderState;

public final class IrisXyCraftLasers {
    private static final RenderPipeline BLIT_AFTER_SHADERPACK = RenderPipeline.builder()
            .withLocation(FTBEvolutionCompanion.id("pipeline/xycraft_laser_blit_after_shaderpack"))
            .withVertexShader("core/screenquad")
            .withFragmentShader(Identifier.fromNamespaceAndPath("xycraft_machines", "core/blit_screen_depth"))
            .withSampler("InSampler")
            .withSampler("InSamplerDepth")
            .withShaderDefine("INCLUDE_COLOR")
            .withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA), 15))
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
            .build();

    private IrisXyCraftLasers() {}

    public static void blitAfterShaderpack() {
        if (!IrisApi.getInstance().isShaderPackInUse()) {
            return;
        }
        XynergyGraphRenderState state = XynergyGraphRenderState.INSTANCE;
        TextureTarget laser = state.LaserRenderTarget;
        if (laser == null || (state.NodeMeshState == null && state.EdgeMeshState == null)) {
            return;
        }
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        if (main.getColorTextureView() == null
                || main.getDepthTextureView() == null
                || laser.getColorTextureView() == null
                || laser.getDepthTextureView() == null) {
            return;
        }
        try (RenderPass pass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(
                        () -> "FTB Evolution: XyCraft lasers after shaderpack",
                        main.getColorTextureView(),
                        OptionalInt.empty(),
                        main.getDepthTextureView(),
                        OptionalDouble.empty())) {
            pass.setPipeline(BLIT_AFTER_SHADERPACK);
            RenderSystem.bindDefaultUniforms(pass);
            pass.bindTexture(
                    "InSampler",
                    laser.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            pass.bindTexture(
                    "InSamplerDepth",
                    laser.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            pass.draw(0, 3);
        }
    }
}
