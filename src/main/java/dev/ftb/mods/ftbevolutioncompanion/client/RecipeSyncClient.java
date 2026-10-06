package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.jei.ClientRecipeMap;

import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

public final class RecipeSyncClient {
    private RecipeSyncClient() {}

    public static void onRecipesReceived(RecipesReceivedEvent event) {
        ClientRecipeMap.set(event.getRecipeMap());
    }
}
