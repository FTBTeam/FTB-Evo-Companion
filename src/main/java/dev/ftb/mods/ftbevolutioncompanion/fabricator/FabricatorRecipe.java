package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidStackTemplate;

import java.util.List;

public record FabricatorRecipe(List<CountedIngredient> ingredients, List<FluidStackTemplate> fluids,
                               List<ItemStackTemplate> results, List<FluidStackTemplate> fluidResults,
                               int ticks, int energyPerTick, String stage) implements Recipe<FabricatorInput> {
    public record CountedIngredient(Ingredient ingredient, int count) {
        public static final Codec<CountedIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("ingredient").forGetter(CountedIngredient::ingredient),
                Codec.intRange(1, 576).fieldOf("count").forGetter(CountedIngredient::count)
        ).apply(instance, CountedIngredient::new));
    }

    private static final MapCodec<FabricatorRecipe> FIELDS = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CountedIngredient.CODEC.listOf(0, 9).optionalFieldOf("ingredients", List.of()).forGetter(FabricatorRecipe::ingredients),
            FluidStackTemplate.CODEC.listOf(0, 2).optionalFieldOf("fluids", List.of()).forGetter(FabricatorRecipe::fluids),
            ItemStackTemplate.CODEC.listOf(0, 3).optionalFieldOf("results", List.of()).forGetter(FabricatorRecipe::results),
            FluidStackTemplate.CODEC.listOf(0, 1).optionalFieldOf("fluid_results", List.of()).forGetter(FabricatorRecipe::fluidResults),
            Codec.intRange(1, 72000).fieldOf("ticks").forGetter(FabricatorRecipe::ticks),
            Codec.intRange(0, 1000000).fieldOf("energy_per_tick").forGetter(FabricatorRecipe::energyPerTick),
            Codec.STRING.optionalFieldOf("stage", "").forGetter(FabricatorRecipe::stage)
    ).apply(instance, FabricatorRecipe::new));

    public static final MapCodec<FabricatorRecipe> CODEC = FIELDS.validate(recipe -> {
        if (recipe.ingredients.isEmpty() && recipe.fluids.isEmpty()) return DataResult.error(() -> "Fabricator recipe needs an input");
        if (recipe.results.isEmpty() && recipe.fluidResults.isEmpty()) return DataResult.error(() -> "Fabricator recipe needs an output");
        if (recipe.fluids.stream().anyMatch(f -> f.amount() > 16000)
                || recipe.fluidResults.stream().anyMatch(f -> f.amount() > 16000)) {
            return DataResult.error(() -> "Fabricator fluid amount exceeds 16000 mB");
        }
        if (recipe.stage.length() > 256 || recipe.stage.chars().anyMatch(Character::isWhitespace)) {
            return DataResult.error(() -> "Stage must be a player scoreboard tag without whitespace");
        }
        return DataResult.success(recipe);
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatorRecipe> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public FabricatorRecipe {
        ingredients = List.copyOf(ingredients);
        fluids = List.copyOf(fluids);
        results = List.copyOf(results);
        fluidResults = List.copyOf(fluidResults);
    }

    public int[] itemAllocation(FabricatorInput input) {
        return IngredientAllocation.allocate(input.items().stream().mapToInt(ItemStack::getCount).toArray(),
                ingredients.stream().mapToInt(CountedIngredient::count).toArray(),
                (slot, requirement) -> ingredients.get(requirement).ingredient.test(input.items().get(slot)));
    }

    public int[] fluidAllocation(FabricatorInput input) {
        return IngredientAllocation.allocate(input.fluids().stream().mapToInt(FluidStack::getAmount).toArray(),
                fluids.stream().mapToInt(FluidStackTemplate::amount).toArray(),
                (tank, requirement) -> FluidStack.isSameFluidSameComponents(input.fluids().get(tank), fluids.get(requirement).create()));
    }

    @Override public boolean matches(FabricatorInput input, Level level) {
        return itemAllocation(input) != null && fluidAllocation(input) != null;
    }
    @Override public ItemStack assemble(FabricatorInput input) { return results.isEmpty() ? ItemStack.EMPTY : results.getFirst().create(); }
    @Override public boolean isSpecial() { return true; }
    @Override public boolean showNotification() { return false; }
    @Override public String group() { return ""; }
    @Override public RecipeSerializer<FabricatorRecipe> getSerializer() { return FabricatorRegistry.SERIALIZER.get(); }
    @Override public RecipeType<FabricatorRecipe> getType() { return FabricatorRegistry.RECIPE_TYPE.get(); }
    @Override public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_MISC; }
}
