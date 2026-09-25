package dev.ftb.mods.ftbevolutioncompanion.metals;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public final class PostNetheriteMetals {
    public static final String NAMESPACE = "ftbmaterials";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NAMESPACE);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(NAMESPACE);

    private static final List<String> ITEM_TYPES = List.of("ingot", "nugget", "raw_ore", "dust", "small_dust", "tiny_dust",
            "dirty_dust", "plate", "gear", "rod", "wire", "chunk", "clump", "cluster", "crystal", "shard");

    private static final List<DeferredItem<Item>> MATERIAL_ITEMS = new ArrayList<>();
    private static final List<DeferredItem<BlockItem>> STORAGE_ITEMS = new ArrayList<>();
    private static final List<DeferredItem<BlockItem>> ORE_ITEMS = new ArrayList<>();

    private record Ore(String suffix, float hardness, SoundType sound, MapColor color) {
    }

    static {
        metal("adamantite", MapColor.COLOR_CYAN,
                new Ore("stone_ore", 8.0F, SoundType.STONE, MapColor.STONE),
                new Ore("deepslate_ore", 9.5F, SoundType.DEEPSLATE, MapColor.DEEPSLATE));
        metal("aeternium", MapColor.COLOR_PURPLE,
                new Ore("nether_ore", 10.0F, SoundType.NETHER_ORE, MapColor.NETHER));
        metal("aurichalcum", MapColor.COLOR_ORANGE,
                new Ore("end_ore", 12.0F, SoundType.STONE, MapColor.SAND));
    }

    private PostNetheriteMetals() {
    }

    private static void metal(String name, MapColor color, Ore... ores) {
        for (String type : ITEM_TYPES) {
            MATERIAL_ITEMS.add(ITEMS.registerItem(name + "_" + type, Item::new, properties -> properties.fireResistant()));
        }
        STORAGE_ITEMS.add(block(name + "_block", properties -> properties.mapColor(color).strength(50.0F, 1200.0F)
                .requiresCorrectToolForDrops().sound(SoundType.NETHERITE_BLOCK)));
        STORAGE_ITEMS.add(block(name + "_raw_block", properties -> properties.mapColor(color).strength(10.0F, 12.0F)
                .requiresCorrectToolForDrops().sound(SoundType.STONE)));
        for (Ore ore : ores) {
            ORE_ITEMS.add(block(name + "_" + ore.suffix(), properties -> properties.mapColor(ore.color())
                    .strength(ore.hardness(), 12.0F).requiresCorrectToolForDrops().sound(ore.sound())));
        }
    }

    private static DeferredItem<BlockItem> block(String name, UnaryOperator<BlockBehaviour.Properties> properties) {
        DeferredBlock<Block> block = BLOCKS.registerBlock(name, Block::new, properties);
        return ITEMS.registerItem(name, itemProperties -> new BlockItem(block.get(), itemProperties),
                itemProperties -> itemProperties.useBlockDescriptionPrefix().fireResistant());
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        eventBus.addListener(PostNetheriteMetals::onBuildCreativeTabs);
    }

    private static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            MATERIAL_ITEMS.forEach(event::accept);
        }
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            STORAGE_ITEMS.forEach(event::accept);
        }
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            ORE_ITEMS.forEach(event::accept);
        }
    }
}
