package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "cy.jdkdigital.productivebees.client.helper.RecipeHelper", remap = false)
public abstract class ProductiveBeesIncubationRecipesMixin {
    @Unique
    private static final Logger ftbevo$LOGGER = LoggerFactory.getLogger("FTBEvoCompanion/ProductiveBeesJei");

    @WrapOperation(method = "getRecipes(Ljava/util/Map;)Ljava/util/List;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStackTemplate;fromNonEmptyStack("
                            + "Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStackTemplate;"))
    private static ItemStackTemplate ftbevo$markMissingEgg(ItemStack stack, Operation<ItemStackTemplate> original,
                                                           @Share("skip") LocalBooleanRef skip) {
        if (stack.isEmpty()) {
            skip.set(true);
            return original.call(new ItemStack(Items.BEE_SPAWN_EGG));
        }
        return original.call(stack);
    }

    @WrapOperation(method = "getRecipes(Ljava/util/Map;)Ljava/util/List;",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private static boolean ftbevo$skipMissingEgg(List<Object> recipes, Object recipe, Operation<Boolean> original,
                                                 @Share("skip") LocalBooleanRef skip) {
        if (skip.get()) {
            skip.set(false);
            ftbevo$LOGGER.warn("Skipping incubation recipe {}: the bee has no spawn egg item",
                    ((RecipeHolder<?>) recipe).id().identifier());
            return false;
        }
        return original.call(recipes, recipe);
    }
}
