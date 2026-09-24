package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRecipe;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRegistry;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public final class FabricatorClient {
    private static List<RecipeHolder<FabricatorRecipe>> recipes = List.of();
    private FabricatorClient() {}
    public static void register(IEventBus bus) {
        bus.addListener(FabricatorClient::screens);
        bus.addListener(FabricatorClient::renderers);
        NeoForge.EVENT_BUS.addListener(FabricatorClient::recipesReceived);
        NeoForge.EVENT_BUS.addListener(FabricatorClient::logout);
    }
    private static void screens(RegisterMenuScreensEvent event) { event.register(FabricatorRegistry.MENU.get(), FabricatorScreen::new); }
    private static void renderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FabricatorRegistry.BLOCK_ENTITY.get(), FabricatorRenderer::new);
    }
    private static void recipesReceived(RecipesReceivedEvent event) {
        recipes = List.copyOf(event.getRecipeMap().byType(FabricatorRegistry.RECIPE_TYPE.get()));
    }
    private static void logout(ClientPlayerNetworkEvent.LoggingOut event) { recipes = List.of(); }
    public static List<RecipeHolder<FabricatorRecipe>> recipes() { return recipes; }
}
