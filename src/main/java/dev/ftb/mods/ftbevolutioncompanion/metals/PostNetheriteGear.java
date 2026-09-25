package dev.ftb.mods.ftbevolutioncompanion.metals;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class PostNetheriteGear {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("ftb");

    public static final TagKey<Block> INCORRECT_FOR_ADAMANTITE_TOOL = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("ftb", "incorrect_for_adamantite_tool"));
    public static final TagKey<Block> MINEABLE_AIOT = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("ftb", "mineable/aiot"));

    private static final List<DeferredItem<? extends Item>> COMBAT = new ArrayList<>();
    private static final List<DeferredItem<? extends Item>> TOOLS = new ArrayList<>();

    private record Tier(String name, int toolDurability, float speed, float attackBonus, int enchantment) {
    }

    static {
        gear(new Tier("adamantite", 3072, 10.0F, 5.0F, 12));
        gear(new Tier("aeternium", 2560, 11.0F, 5.0F, 25));
        gear(new Tier("aurichalcum", 4096, 12.0F, 6.0F, 20));
    }

    private PostNetheriteGear() {
    }

    private static TagKey<Item> ingots(String metal) {
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/" + metal));
    }

    private static void gear(Tier tier) {
        String n = tier.name();
        ToolMaterial tool = new ToolMaterial(INCORRECT_FOR_ADAMANTITE_TOOL, tier.toolDurability(), tier.speed(),
                tier.attackBonus(), tier.enchantment(), ingots(n));

        COMBAT.add(ITEMS.registerItem(n + "_sword", Item::new, p -> p.sword(tool, 3.0F, -2.4F).fireResistant()));
        COMBAT.add(ITEMS.registerItem(n + "_spear", Item::new,
                p -> p.spear(tool, 1.15F, 1.2F, 0.4F, 2.5F, 9.0F, 5.5F, 5.1F, 8.75F, 4.6F).fireResistant()));
        TOOLS.add(ITEMS.registerItem(n + "_pickaxe", Item::new, p -> p.pickaxe(tool, 1.0F, -2.8F).fireResistant()));
        TOOLS.add(ITEMS.registerItem(n + "_axe", p -> new AxeItem(tool, 5.0F, -3.0F, p), p -> p.fireResistant()));
        TOOLS.add(ITEMS.registerItem(n + "_shovel", p -> new ShovelItem(tool, 1.5F, -3.0F, p), p -> p.fireResistant()));
        TOOLS.add(ITEMS.registerItem(n + "_hoe", p -> new HoeItem(tool, -4.0F, 0.0F, p), p -> p.fireResistant()));
        TOOLS.add(ITEMS.registerItem(n + "_aiot", AiotItem::new, p -> p.tool(tool, MINEABLE_AIOT, 4.0F, -3.0F, 0.0F).fireResistant()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
        eventBus.addListener(PostNetheriteGear::onBuildCreativeTabs);
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            COMBAT.forEach(event::accept);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            TOOLS.forEach(event::accept);
        }
    }
}
