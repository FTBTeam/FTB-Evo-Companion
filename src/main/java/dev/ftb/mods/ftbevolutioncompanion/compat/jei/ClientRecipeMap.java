package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import net.minecraft.world.item.crafting.RecipeMap;

public final class ClientRecipeMap {
    private static volatile RecipeMap recipes = RecipeMap.EMPTY;

    private ClientRecipeMap() {}

    public static RecipeMap get() {
        return recipes;
    }

    public static void set(RecipeMap map) {
        recipes = map == null ? RecipeMap.EMPTY : map;
    }
}
