package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class HephaestusTools {
    private static final Logger LOGGER = LoggerFactory.getLogger(HephaestusTools.class);
    private static final String ROOT = "com.titammods.hephaestus_tools.";
    private static final Identifier CONSTRUCTION = Identifier.fromNamespaceAndPath("hephaestus_tools", "construction");
    public static final TagKey<Block> INCORRECT_FOR_ADAMANTITE =
            TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("ftbarmory", "incorrect_for_adamantite_tool"));
    public static final Set<String> REMOVED_MATERIALS = Set.of("lumium", "signalum", "constantan");

    private static Object[] tiers;
    private static Method materials;
    private static Method materialIdentifier;
    private static Method managerInstance;
    private static Method managerStats;
    private static Method statsTier;
    private static Method harvestTier;
    private static Method incorrectFor;
    private static Object jagged;
    private static Method modifyAttackDamage;
    private static boolean failed;

    private HephaestusTools() {}

    private static synchronized boolean init() {
        if (failed) {
            return false;
        }
        if (materials != null) {
            return true;
        }
        try {
            Class<?> tierClass = Class.forName(ROOT + "tools.stat.HarvestTier");
            tiers = tierClass.getEnumConstants();
            materials = Class.forName(ROOT + "tools.nbt.ToolConstructionData").getMethod("materials");
            materialIdentifier = Class.forName(ROOT + "materials.MaterialId").getMethod("id");
            Class<?> manager = Class.forName(ROOT + "materials.MaterialManager");
            managerInstance = manager.getMethod("getInstance");
            managerStats = manager.getMethod("getStats", Class.forName(ROOT + "materials.MaterialId"));
            statsTier = Class.forName(ROOT + "materials.MaterialStats").getMethod("tierOrdinal");
            harvestTier = Class.forName(ROOT + "tools.nbt.ToolStack").getMethod("getHarvestTier", ItemStack.class);
            incorrectFor = tierClass.getMethod("incorrectForTag");
            Class<?> trait = Class.forName(ROOT + "materials.trait.MaterialTrait");
            jagged = trait.getMethod("valueOf", String.class).invoke(null, "JAGGED");
            modifyAttackDamage = trait.getMethod(
                    "modifyAttackDamage", Player.class, LivingEntity.class, ItemStack.class, float.class);
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            failed = true;
            LOGGER.error("Hephaestus Tools hooks are unavailable", e);
            return false;
        }
    }

    public static Object tier(int ordinal) {
        return init() ? tiers[ordinal] : null;
    }

    private static List<Object> rawMaterials(ItemStack stack) {
        if (stack.isEmpty()) {
            return List.of();
        }
        var type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(CONSTRUCTION);
        if (type == null) {
            return List.of();
        }
        Object data = stack.get(type);
        if (data == null || !init()) {
            return List.of();
        }
        try {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) materials.invoke(data);
            return list;
        } catch (ReflectiveOperationException e) {
            return List.of();
        }
    }

    public static List<Identifier> materials(ItemStack stack) {
        List<Identifier> ids = new ArrayList<>();
        for (Object material : rawMaterials(stack)) {
            Identifier id = materialId(material);
            if (id != null) {
                ids.add(id);
            }
        }
        return ids;
    }

    public static Identifier materialId(Object material) {
        if (material == null || !init()) {
            return null;
        }
        try {
            return (Identifier) materialIdentifier.invoke(material);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    public static int headTier(ItemStack stack) {
        List<Object> list = rawMaterials(stack);
        if (list.isEmpty()) {
            return -1;
        }
        try {
            Object stats = managerStats.invoke(managerInstance.invoke(null), list.get(0));
            return stats == null ? -1 : (int) statsTier.invoke(stats);
        } catch (ReflectiveOperationException e) {
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    public static TagKey<Block> incorrectFor(ItemStack stack) {
        if (headTier(stack) >= 6) {
            return INCORRECT_FOR_ADAMANTITE;
        }
        if (!init()) {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
        try {
            return (TagKey<Block>) incorrectFor.invoke(harvestTier.invoke(null, stack));
        } catch (ReflectiveOperationException e) {
            return BlockTags.INCORRECT_FOR_WOODEN_TOOL;
        }
    }

    public static float jagged(Player player, LivingEntity target, ItemStack stack, float amount) {
        if (!init()) {
            return amount;
        }
        try {
            return (float) modifyAttackDamage.invoke(jagged, player, target, stack, amount);
        } catch (ReflectiveOperationException e) {
            return amount;
        }
    }
}
