package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sfm.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.compat.jei.ClientRecipeMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "ca.teamdman.sfm.client.jei.SFMJEIPlugin", remap = false)
public abstract class SfmJeiPluginMixin {
    @WrapOperation(
            method = "registerRecipes",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/server/MinecraftServer;getRecipeManager()Lnet/minecraft/world/item/crafting/RecipeManager;"))
    private RecipeManager ftbevo$noIntegratedServer(MinecraftServer server, Operation<RecipeManager> original) {
        return server == null ? null : original.call(server);
    }

    @WrapOperation(
            method = "registerRecipes",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/crafting/RecipeManager;recipeMap()Lnet/minecraft/world/item/crafting/RecipeMap;"))
    private RecipeMap ftbevo$clientRecipes(RecipeManager manager, Operation<RecipeMap> original) {
        return manager == null ? ClientRecipeMap.get() : original.call(manager);
    }
}
