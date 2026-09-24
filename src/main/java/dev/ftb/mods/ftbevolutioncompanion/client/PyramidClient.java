package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.pyramid.PyramidRegistry;

import net.minecraft.core.BlockPos;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class PyramidClient {
    private PyramidClient() {
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PyramidRegistry.SKYLINE.get(), SkylineRenderer::new);
    }

    public static void openScreen(BlockPos pos, long chapterId) {
        new PyramidTaskScreen(pos, chapterId).openGui();
    }
}
