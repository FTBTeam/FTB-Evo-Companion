package dev.ftb.mods.ftbevolutioncompanion.content;

import com.mojang.serialization.MapCodec;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OddBerryBushBlock extends VegetationBlock implements BonemealableBlock {
    public static final MapCodec<OddBerryBushBlock> CODEC = simpleCodec(OddBerryBushBlock::new);

    public static final int MAX_AGE = 2;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_2;
    public static final BooleanProperty TENDED = BooleanProperty.create("tended");

    private static final TagKey<Item> BERRIES = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath("rootsclassic", "berries"));

    private static final VoxelShape SHAPE_SPROUT = Block.column(8.0, 0.0, 6.0);
    private static final VoxelShape SHAPE_GROWN = Block.column(12.0, 0.0, 11.0);

    public OddBerryBushBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0).setValue(TENDED, false));
    }

    @Override
    protected MapCodec<OddBerryBushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE, TENDED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AGE) == 0 ? SHAPE_SPROUT : SHAPE_GROWN;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(CompanionTags.ODD_BERRY_BUSH_SPREADABLE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player && !state.getValue(TENDED)) {
            level.setBlock(pos, state.setValue(TENDED, true), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (player.isSecondaryUseActive()) {
            boolean tended = !state.getValue(TENDED);
            level.setBlock(pos, state.setValue(TENDED, tended), Block.UPDATE_CLIENTS);
            level.playSound(null, pos, tended ? SoundEvents.CROP_PLANTED : SoundEvents.GRASS_BREAK,
                    SoundSource.BLOCKS, 0.8f, tended ? 1.1f : 0.9f);
            player.sendOverlayMessage(Component.translatable(tended
                    ? "block.ftbevolutioncompanion.odd_berry_bush.tended"
                    : "block.ftbevolutioncompanion.odd_berry_bush.wild"));
            return InteractionResult.SUCCESS;
        }

        if (state.getValue(AGE) < MAX_AGE) {
            return InteractionResult.PASS;
        }

        RandomSource random = level.getRandom();
        int picked = 1 + random.nextInt(3);

        for (int i = 0; i < picked; i++) {
            BuiltInRegistries.ITEM.get(BERRIES)
                    .flatMap(berries -> berries.getRandomElement(random))
                    .ifPresent(berry -> popResource(level, pos, new ItemStack(berry.value())));
        }

        level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.8f);
        level.setBlock(pos, state.setValue(AGE, MAX_AGE - 1), Block.UPDATE_CLIENTS);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE || state.getValue(TENDED);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);

        if (age < MAX_AGE) {
            if (level.getRawBrightness(pos.above(), 0) >= 9 && random.nextInt(CompanionConfig.ODD_BERRY_BUSH_GROWTH_CHANCE.get()) == 0) {
                level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
            }
            return;
        }

        trySpread(state, level, pos, random);
    }

    private void trySpread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(TENDED) || !CompanionConfig.ODD_BERRY_BUSH_SPREAD.get()) {
            return;
        }

        if (random.nextInt(CompanionConfig.ODD_BERRY_BUSH_SPREAD_CHANCE.get()) != 0) {
            return;
        }

        int remaining = CompanionConfig.ODD_BERRY_BUSH_MAX_NEARBY.get();

        for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
            if (level.getBlockState(nearby).is(this) && --remaining <= 0) {
                return;
            }
        }

        BlockState sprout = defaultBlockState().setValue(TENDED, true);
        BlockPos target = pos;

        for (int attempt = 0; attempt < 4; attempt++) {
            BlockPos candidate = target.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

            if (level.isEmptyBlock(candidate) && sprout.canSurvive(level, candidate)) {
                level.setBlock(candidate, sprout, Block.UPDATE_CLIENTS);
                return;
            }

            target = candidate;
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        level.setBlock(pos, state.setValue(AGE, Math.min(MAX_AGE, state.getValue(AGE) + 1)), Block.UPDATE_CLIENTS);
    }
}
