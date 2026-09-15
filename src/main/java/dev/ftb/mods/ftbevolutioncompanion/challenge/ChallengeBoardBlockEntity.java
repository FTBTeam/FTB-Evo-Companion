package dev.ftb.mods.ftbevolutioncompanion.challenge;

import dev.ftb.mods.ftbevolutioncompanion.config.CompanionConfig;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ChallengeBoardBlockEntity extends BlockEntity {
    public static final int MAX_SIZE = 9;

    private int rank = 1;
    private int width = 1;
    private int height = 1;

    public ChallengeBoardBlockEntity(BlockPos pos, BlockState state) {
        super(ChallengeRegistry.CHALLENGE_BOARD.get(), pos, state);
    }

    public int getRank() {
        return rank;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRank(int rank) {
        this.rank = Math.max(1, rank);
        sync();
    }

    public void setSize(int width, int height) {
        this.width = Mth.clamp(width, 1, MAX_SIZE);
        this.height = Mth.clamp(height, 1, MAX_SIZE);
        sync();
    }

    public void cycleRank() {
        setRank(rank % CompanionConfig.CHALLENGE_BOARD_SIZE.get() + 1);
    }

    private void sync() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level instanceof ServerLevel serverLevel) {
            ChallengeBoardBlock.removeAux(serverLevel, pos, this, state.getValue(ChallengeBoardBlock.FACING));
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        rank = Math.max(1, input.getIntOr("rank", 1));
        width = Mth.clamp(input.getIntOr("width", 1), 1, MAX_SIZE);
        height = Mth.clamp(input.getIntOr("height", 1), 1, MAX_SIZE);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("rank", rank);
        output.putInt("width", width);
        output.putInt("height", height);
    }
}
