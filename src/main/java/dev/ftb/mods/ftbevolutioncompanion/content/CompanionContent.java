package dev.ftb.mods.ftbevolutioncompanion.content;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CompanionContent {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBEvolutionCompanion.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FTBEvolutionCompanion.MOD_ID);

    public static final DeferredBlock<OddBerryBushBlock> ODD_BERRY_BUSH = BLOCKS.registerBlock(
            "odd_berry_bush",
            OddBerryBushBlock::new,
            properties -> properties
                    .mapColor(MapColor.PLANT)
                    .noCollision()
                    .randomTicks()
                    .instabreak()
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .offsetType(BlockBehaviour.OffsetType.XZ)
                    .pushReaction(PushReaction.DESTROY)
                    .ignitedByLava()
    );

    public static final DeferredItem<OddBerryBushItem> ODD_BERRY_BUSH_ITEM = ITEMS.registerItem(
            "odd_berry_bush",
            properties -> new OddBerryBushItem(ODD_BERRY_BUSH.get(), properties),
            properties -> properties.useBlockDescriptionPrefix()
    );

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ODD_BERRY_BUSH_ITEM);
        }
    }

    private CompanionContent() {
    }
}
