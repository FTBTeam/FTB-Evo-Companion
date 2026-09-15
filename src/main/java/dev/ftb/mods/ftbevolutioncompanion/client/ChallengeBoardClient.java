package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeClientData;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeRegistry;

import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class ChallengeBoardClient {
    private ChallengeBoardClient() {
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ChallengeRegistry.CHALLENGE_BOARD.get(), ChallengeBoardRenderer::new);
    }

    public static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ChallengeClientData.clear();
    }
}
