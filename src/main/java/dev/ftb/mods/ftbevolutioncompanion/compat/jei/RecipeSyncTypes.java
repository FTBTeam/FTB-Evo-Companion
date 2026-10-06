package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

import java.util.List;

public final class RecipeSyncTypes {
    private static final List<Identifier> TYPES = List.of(Identifier.fromNamespaceAndPath("sfm", "printing_press"));

    private RecipeSyncTypes() {}

    public static void onDatapackSync(OnDatapackSyncEvent event) {
        for (Identifier id : TYPES) {
            BuiltInRegistries.RECIPE_TYPE.getOptional(id).ifPresent(event::sendRecipes);
        }
    }
}
