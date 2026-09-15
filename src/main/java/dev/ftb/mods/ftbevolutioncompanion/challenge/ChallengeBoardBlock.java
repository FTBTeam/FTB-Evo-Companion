package dev.ftb.mods.ftbevolutioncompanion.challenge;

import com.mojang.serialization.MapCodec;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;
import dev.ftb.mods.ftbquests.net.OpenQuestBookMessage;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.ServerQuestFile;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;

import net.neoforged.neoforge.network.PacketDistributor;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ChallengeBoardBlock extends BaseEntityBlock {
    public static final MapCodec<ChallengeBoardBlock> CODEC = simpleCodec(ChallengeBoardBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final int REMOVE_FLAGS = Block.UPDATE_ALL | Block.UPDATE_SKIP_BLOCK_ENTITY_SIDEEFFECTS;

    public ChallengeBoardBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends ChallengeBoardBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChallengeBoardBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        ChallengeBoardBlockEntity board = coreAt(level, pos).orElse(null);
        if (board == null) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        boolean editor = player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER) && player.getMainHandItem().isEmpty();
        if (editor && player.isSecondaryUseActive()) {
            board.cycleRank();
            return InteractionResult.SUCCESS_SERVER;
        }

        if (player instanceof ServerPlayer serverPlayer && ServerQuestFile.exists()) {
            Chapter chapter = ChallengeLeaderboard.findChapter(ServerQuestFile.getInstance());
            if (chapter != null) {
                PacketDistributor.sendToPlayer(serverPlayer, new OpenQuestBookMessage(chapter.id));
            }
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    public static Optional<ChallengeBoardBlockEntity> coreAt(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ChallengeBoardBlockEntity core) return Optional.of(core);
        if (blockEntity instanceof ChallengeBoardAuxBlockEntity aux) return aux.core();
        return Optional.empty();
    }

    public static BoundingBox bounds(BlockPos corePos, int width, int height, Direction facing) {
        int leftCount = (width - 1) / 2;
        int rightCount = width - 1 - leftCount;
        BlockPos leftBottom = corePos.relative(facing.getClockWise(), leftCount);
        BlockPos rightTop = corePos.relative(facing.getCounterClockWise(), rightCount).above(height - 1);
        return BoundingBox.fromCorners(leftBottom, rightTop);
    }

    public static boolean isAuxOf(Level level, BlockPos pos, BlockPos corePos) {
        return level.getBlockEntity(pos) instanceof ChallengeBoardAuxBlockEntity aux && aux.belongsTo(corePos);
    }

    @Nullable
    public static BlockPos resize(ServerLevel level, ChallengeBoardBlockEntity core, int width, int height) {
        BlockPos corePos = core.getBlockPos();
        Direction facing = core.getBlockState().getValue(FACING);
        BoundingBox oldBounds = bounds(corePos, core.getWidth(), core.getHeight(), facing);
        BoundingBox newBounds = bounds(corePos, width, height, facing);

        List<BlockPos> wanted = BlockPos.betweenClosedStream(newBounds)
                .filter(pos -> !pos.equals(corePos) && !isAuxOf(level, pos, corePos))
                .map(BlockPos::immutable)
                .toList();
        for (BlockPos pos : wanted) {
            if (!level.getBlockState(pos).canBeReplaced()) return pos;
        }

        List<BlockPos> stale = BlockPos.betweenClosedStream(oldBounds)
                .filter(pos -> !newBounds.isInside(pos) && isAuxOf(level, pos, corePos))
                .map(BlockPos::immutable)
                .toList();
        stale.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS));

        BlockState auxState = CompanionContent.CHALLENGE_BOARD_AUX.get().defaultBlockState().setValue(FACING, facing);
        wanted.forEach(pos -> {
            level.setBlockAndUpdate(pos, auxState);
            if (level.getBlockEntity(pos) instanceof ChallengeBoardAuxBlockEntity aux) {
                aux.setCore(corePos);
            }
        });

        core.setSize(width, height);
        return null;
    }

    public static void removeAux(ServerLevel level, BlockPos corePos, ChallengeBoardBlockEntity core, Direction facing) {
        BoundingBox box = bounds(corePos, core.getWidth(), core.getHeight(), facing);
        List<BlockPos> aux = BlockPos.betweenClosedStream(box)
                .filter(pos -> isAuxOf(level, pos, corePos))
                .map(BlockPos::immutable)
                .toList();
        aux.forEach(pos -> level.setBlock(pos, Blocks.AIR.defaultBlockState(), REMOVE_FLAGS));
    }
}
