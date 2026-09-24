package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorMenu;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorPayloads;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRecipe;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRegistry;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorTransfer;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record FabricatorTransferHandler(IRecipeTransferHandlerHelper helper)
        implements IRecipeTransferHandler<FabricatorMenu, RecipeHolder<FabricatorRecipe>> {
    @Override public Class<FabricatorMenu> getContainerClass() { return FabricatorMenu.class; }
    @Override public Optional<MenuType<FabricatorMenu>> getMenuType() { return Optional.of(FabricatorRegistry.MENU.get()); }
    @Override public IRecipeType<RecipeHolder<FabricatorRecipe>> getRecipeType() { return FabricatorCategory.TYPE; }
    @Override public @Nullable IRecipeTransferError transferRecipe(FabricatorMenu menu, RecipeHolder<FabricatorRecipe> recipe,
                                                                 IRecipeSlotsView slots, Player player, boolean maximum, boolean transfer) {
        if (FabricatorTransfer.plan(menu, player.getInventory(), recipe.value(), maximum) == null) {
            return helper.createUserErrorWithTooltip(Component.translatable("fabricator.ftbevolutioncompanion.transfer_missing"));
        }
        if (transfer) ClientPacketDistributor.sendToServer(new FabricatorPayloads.Transfer(menu.containerId, recipe.id().identifier(), maximum));
        return null;
    }
}
