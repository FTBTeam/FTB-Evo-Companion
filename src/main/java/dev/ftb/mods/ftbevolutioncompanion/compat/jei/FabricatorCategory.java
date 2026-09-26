package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRecipe;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorRegistry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public final class FabricatorCategory implements IRecipeCategory<RecipeHolder<FabricatorRecipe>> {
    public static final IRecipeHolderType<FabricatorRecipe> TYPE = IRecipeHolderType.create(FTBEvolutionCompanion.id("fabricating"));
    private static final String LANG = "fabricator.ftbevolutioncompanion.";
    private final IDrawable icon;
    public FabricatorCategory(IGuiHelper helper) { icon = helper.createDrawableItemLike(FabricatorRegistry.ITEM.get()); }
    @Override public IRecipeHolderType<FabricatorRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("block.ftbevolutioncompanion.ftb_fabricator"); }
    @Override public int getWidth() { return 192; }
    @Override public int getHeight() { return 120; }
    @Override public IDrawable getIcon() { return icon; }
    @Override public Identifier getIdentifier(RecipeHolder<FabricatorRecipe> holder) { return holder.id().identifier(); }
    @Override public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FabricatorRecipe> holder, IFocusGroup focuses) {
        FabricatorRecipe recipe = holder.value();
        for (int i = 0; i < recipe.ingredients().size(); i++) {
            var input = recipe.ingredients().get(i);
            builder.addSlot(RecipeIngredientRole.INPUT, 5 + i % 3 * 18, 5 + i / 3 * 18)
                    .addItemStacks(input.ingredient().items().map(item -> new ItemStack(item, input.count())).toList())
                    .setStandardSlotBackground()
                    .addRichTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable(LANG + "required_count", input.count())));
        }
        for (int i = 0; i < recipe.results().size(); i++) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 130 + i * 20, 23).add(recipe.results().get(i)).setStandardSlotBackground();
        }
        for (int i = 0; i < recipe.fluids().size(); i++) {
            var fluid = recipe.fluids().get(i).create();
            builder.addSlot(RecipeIngredientRole.INPUT, 64 + i * 20, 5)
                    .add(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())
                    .setFluidRenderer(fluid.getAmount(), false, 16, 48);
        }
        for (int i = 0; i < recipe.fluidResults().size(); i++) {
            var fluid = recipe.fluidResults().get(i).create();
            builder.addSlot(RecipeIngredientRole.OUTPUT, 164, 47)
                    .add(fluid.getFluid(), fluid.getAmount(), fluid.getComponentsPatch())
                    .setFluidRenderer(fluid.getAmount(), false, 16, 20);
        }
    }
    @Override public void draw(RecipeHolder<FabricatorRecipe> holder, IRecipeSlotsView slots, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;
        var recipe = holder.value();
        graphics.text(font, "→", 107, 27, 0xFF2CA8AF, false);
        graphics.text(font, Component.translatable(LANG + "recipe_power", recipe.energyPerTick(), recipe.ticks() / 20F), 4, 72, 0xFF444444, false);
        graphics.text(font, Component.translatable(LANG + "recipe_energy", (long) recipe.energyPerTick() * recipe.ticks()), 4, 84, 0xFF444444, false);
        Component stage = recipe.stage().isEmpty() ? Component.translatable(LANG + "no_stage") : Component.translatable(LANG + "stage", FabricatorRecipe.stageName(recipe.stage()));
        List<FormattedCharSequence> lines = font.split(stage, 184);
        for (int i = 0; i < Math.min(2, lines.size()); i++) graphics.text(font, lines.get(i), 4, 98 + i * 10, 0xFF166C7D, false);
    }
    @Override public void getTooltip(ITooltipBuilder tooltip, RecipeHolder<FabricatorRecipe> holder, IRecipeSlotsView slots, double mouseX, double mouseY) {
        if (mouseY >= 96 && !holder.value().stage().isEmpty()) tooltip.add(Component.translatable(LANG + "stage", FabricatorRecipe.stageName(holder.value().stage())));
    }
}
