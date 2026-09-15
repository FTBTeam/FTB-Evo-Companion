package dev.ftb.mods.ftbevolutioncompanion.content;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardAuxBlock;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CompanionContent {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FTBEvolutionCompanion.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(FTBEvolutionCompanion.MOD_ID);
    public static final DeferredRegister.Blocks FTB_BLOCKS = DeferredRegister.createBlocks("ftb");
    public static final DeferredRegister.Items FTB_ITEMS = DeferredRegister.createItems("ftb");

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

    public static final DeferredBlock<Block> NETHER_BEDROCK = FTB_BLOCKS.registerBlock(
            "nether_bedrock",
            Block::new,
            properties -> properties
                    .mapColor(MapColor.NETHER)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.NETHERRACK)
                    .noLootTable()
                    .isValidSpawn((state, level, pos, entityType) -> false)
    );

    public static final DeferredBlock<Block> END_BEDROCK = FTB_BLOCKS.registerBlock(
            "end_bedrock",
            Block::new,
            properties -> properties
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .isValidSpawn((state, level, pos, entityType) -> false)
    );

    public static final DeferredItem<BlockItem> NETHER_BEDROCK_ITEM = FTB_ITEMS.registerItem(
            "nether_bedrock",
            properties -> new BlockItem(NETHER_BEDROCK.get(), properties),
            properties -> properties.useBlockDescriptionPrefix()
    );

    public static final DeferredItem<BlockItem> END_BEDROCK_ITEM = FTB_ITEMS.registerItem(
            "end_bedrock",
            properties -> new BlockItem(END_BEDROCK.get(), properties),
            properties -> properties.useBlockDescriptionPrefix()
    );

    public static final DeferredBlock<ChallengeBoardBlock> CHALLENGE_BOARD = BLOCKS.registerBlock(
            "challenge_board",
            ChallengeBoardBlock::new,
            properties -> properties
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3F)
                    .sound(SoundType.METAL)
    );

    public static final DeferredBlock<ChallengeBoardAuxBlock> CHALLENGE_BOARD_AUX = BLOCKS.registerBlock(
            "challenge_board_aux",
            ChallengeBoardAuxBlock::new,
            properties -> properties
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(0.3F)
                    .sound(SoundType.METAL)
                    .noLootTable()
    );

    public static final DeferredItem<BlockItem> CHALLENGE_BOARD_ITEM = ITEMS.registerItem(
            "challenge_board",
            properties -> new BlockItem(CHALLENGE_BOARD.get(), properties),
            properties -> properties.useBlockDescriptionPrefix()
    );

    public static void onBuildCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ODD_BERRY_BUSH_ITEM);
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(NETHER_BEDROCK_ITEM);
            event.accept(END_BEDROCK_ITEM);
            event.accept(CHALLENGE_BOARD_ITEM);
        }
    }

    private CompanionContent() {
    }
}
