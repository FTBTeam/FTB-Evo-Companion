package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class BeastTrophyClient {
    private BeastTrophyClient() {
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CompanionContent.BEAST_TROPHY_BLOCK_ENTITY.get(), BeastTrophyRenderer::new);
    }
}
