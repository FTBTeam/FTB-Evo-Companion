package dev.ftb.mods.ftbevolutioncompanion.compat.jei;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.simplywinged.ParagliderWings;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;

import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class FTBEvoJeiPlugin implements IModPlugin {
    private static final String SIMPLY_WINGED = "in2bubble_simply_winged";

    private static final List<WingedArmor> WINGED_ARMORS = List.of(
            new WingedArmor(Items.LEATHER_HORSE_ARMOR, "leather", "Winged Leather Horse Armor"),
            new WingedArmor(Items.COPPER_HORSE_ARMOR, "copper", "Winged Copper Horse Armor"),
            new WingedArmor(Items.IRON_HORSE_ARMOR, "iron", "Winged Iron Horse Armor"),
            new WingedArmor(Items.GOLDEN_HORSE_ARMOR, "golden", "Winged Golden Horse Armor"),
            new WingedArmor(Items.DIAMOND_HORSE_ARMOR, "diamond", "Winged Diamond Horse Armor"),
            new WingedArmor(Items.NETHERITE_HORSE_ARMOR, "netherite", "Winged Netherite Horse Armor"));

    @Override
    public Identifier getPluginUid() {
        return FTBEvolutionCompanion.id("jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!ModList.get().isLoaded(SIMPLY_WINGED)) {
            return;
        }

        List<ItemStack> wings = new ArrayList<>();
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(ParagliderWings.PARAGLIDERS)) {
            wings.add(new ItemStack(holder));
        }

        if (wings.isEmpty()) {
            return;
        }

        registration.addItemStackInfo(wings,
                Component.translatable("jei.ftbevolutioncompanion.pegasus.conversion"),
                Component.translatable("jei.ftbevolutioncompanion.pegasus.flight"));

        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();
        List<IJeiAnvilRecipe> anvilRecipes = new ArrayList<>();

        for (WingedArmor armor : WINGED_ARMORS) {
            List<ItemStack> outputs = wings.stream().map(wing -> wingedArmorStack(armor, wing)).toList();

            anvilRecipes.add(factory.createAnvilRecipe(
                    new ItemStack(armor.armor()),
                    wings,
                    outputs,
                    FTBEvolutionCompanion.id("winged_horse_armor/" + armor.path())));
        }

        registration.addRecipes(RecipeTypes.ANVIL, anvilRecipes);
    }

    private static ItemStack wingedArmorStack(WingedArmor armor, ItemStack wing) {
        ItemStack stack = new ItemStack(armor.armor());
        int maxDamage = wing.getMaxDamage();

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putBoolean("in2bubble_winged", true);
        tag.putInt("in2bubble_elytra_damage", 0);
        tag.putInt("in2bubble_elytra_max_damage", maxDamage);
        tag.putBoolean("in2bubble_visible_damage_synced", true);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        if (maxDamage > 0) {
            stack.set(DataComponents.MAX_DAMAGE, maxDamage);
            stack.set(DataComponents.DAMAGE, 0);
        }

        stack.set(DataComponents.ITEM_NAME, Component.literal(armor.displayName()).withStyle(ChatFormatting.GOLD));
        stack.set(DataComponents.LORE, new ItemLore(
                List.of(Component.literal("Upgrade: Winged Horse").withStyle(ChatFormatting.AQUA))));
        stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);

        return stack;
    }

    private record WingedArmor(Item armor, String path, String displayName) {
    }
}
