package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import com.mojang.serialization.MapCodec;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.network.PyramidPayloads;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbteams.api.Team;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;

import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EvolutionPyramidBlock extends BaseEntityBlock {
    public static final MapCodec<EvolutionPyramidBlock> CODEC = simpleCodec(EvolutionPyramidBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final String LANG = "ftbevolutioncompanion.evolution_pyramid.";

    public EvolutionPyramidBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends EvolutionPyramidBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PyramidLayout.shape(Vec3i.ZERO, state.getValue(FACING));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return true;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos corePos = context.getClickedPos();
        Player player = context.getPlayer();

        String problem = null;
        for (BlockPos pos : PyramidLayout.positions(corePos, facing)) {
            if (pos.equals(corePos)) continue;
            if (!level.isInsideBuildHeight(pos) || !level.getWorldBorder().isWithinBounds(pos)) {
                problem = "no_room";
                break;
            }
            if (!level.getBlockState(pos).canBeReplaced()) {
                problem = "blocked";
                break;
            }
            if (!level.isClientSide() && player != null
                    && EventHooks.onBlockPlace(player, BlockSnapshot.create(level.dimension(), level, pos), Direction.UP)) {
                problem = "protected";
                break;
            }
        }

        if (problem != null) {
            if (!level.isClientSide() && player != null) {
                player.sendOverlayMessage(Component.translatable(LANG + problem));
            }
            return null;
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        super.setPlacedBy(level, pos, state, by, itemStack);
        if (level.isClientSide()) return;

        Direction facing = state.getValue(FACING);
        BlockState partState = CompanionContent.EVOLUTION_PYRAMID_PART.get().defaultBlockState();
        List<BlockPos> placed = new ArrayList<>();
        for (BlockPos partPos : PyramidLayout.positions(pos, facing)) {
            if (partPos.equals(pos)) continue;
            level.setBlock(partPos, partState, Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(partPos) instanceof EvolutionPyramidPartBlockEntity part) {
                part.setCore(pos);
                placed.add(partPos);
            }
        }

        if (level.getBlockEntity(pos) instanceof EvolutionPyramidBlockEntity machine && by != null) {
            machine.setOwner(by.getUUID());
        }

        for (BlockPos partPos : placed) {
            level.sendBlockUpdated(partPos, partState, partState, Block.UPDATE_CLIENTS);
            level.updateNeighborsAt(partPos, partState.getBlock());
            level.invalidateCapabilities(partPos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return use(level, pos, player);
    }

    static InteractionResult use(Level level, BlockPos pos, Player player) {
        Optional<EvolutionPyramidBlockEntity> found = coreAt(level, pos);
        if (found.isEmpty()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.SUCCESS_SERVER;

        EvolutionPyramidBlockEntity machine = found.get();
        if (!PyramidQuests.isMember(serverPlayer, machine.getOwner())) {
            Component owner = PyramidQuests.ownerTeam(machine.getOwner()).map(Team::getColoredName)
                    .orElse(Component.translatable(LANG + "unknown_owner"));
            serverPlayer.sendOverlayMessage(Component.translatable(LANG + "not_owner", owner));
            return InteractionResult.SUCCESS_SERVER;
        }

        Chapter chapter = PyramidQuests.chapter();
        if (chapter == null) {
            serverPlayer.sendOverlayMessage(Component.translatable(LANG + "no_chapter"));
            return InteractionResult.SUCCESS_SERVER;
        }

        PacketDistributor.sendToPlayer(serverPlayer, new PyramidPayloads.OpenPyramidScreen(machine.getBlockPos(), chapter.id));
        return InteractionResult.SUCCESS_SERVER;
    }

    public static Optional<EvolutionPyramidBlockEntity> coreAt(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof EvolutionPyramidBlockEntity core) return Optional.of(core);
        if (blockEntity instanceof EvolutionPyramidPartBlockEntity part) return part.core();
        return Optional.empty();
    }

    public static boolean isPartOf(Level level, BlockPos pos, BlockPos corePos) {
        return level.getBlockEntity(pos) instanceof EvolutionPyramidPartBlockEntity part && part.belongsTo(corePos);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EvolutionPyramidBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, PyramidRegistry.EVOLUTION_PYRAMID.get(), EvolutionPyramidBlockEntity::serverTick);
    }
}
