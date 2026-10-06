package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.compat.powerarmor.PowerArmorRecipeSync;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.portingdeadmods.power_armor.compat.PAJeiPlugin", remap = false)
public abstract class PowerArmorJeiRecipesMixin {
    @WrapOperation(
            method = "registerRecipes(Lmezz/jei/api/registration/IRecipeRegistration;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/client/multiplayer/ClientLevel;recipeAccess()"
                                    + "Lnet/minecraft/world/item/crafting/RecipeAccess;"))
    private RecipeAccess ftbevo$syncedCompressingRecipes(ClientLevel level, Operation<RecipeAccess> original) {
        RecipeManager manager = new RecipeManager(level.registryAccess());
        ((RecipeManagerAccessor) manager).ftbevo$setRecipes(PowerArmorRecipeSync.recipes());
        return manager;
    }
}
