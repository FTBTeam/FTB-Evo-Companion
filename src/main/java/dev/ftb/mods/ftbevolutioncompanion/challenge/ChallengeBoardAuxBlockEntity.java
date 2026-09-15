package dev.ftb.mods.ftbevolutioncompanion.challenge;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class ChallengeBoardAuxBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos coreOffset;

    public ChallengeBoardAuxBlockEntity(BlockPos pos, BlockState state) {
        super(ChallengeRegistry.CHALLENGE_BOARD_AUX.get(), pos, state);
    }

    public void setCore(BlockPos corePos) {
        coreOffset = corePos.subtract(worldPosition);
        setChanged();
    }

    public void detach() {
        coreOffset = null;
    }

    public Optional<BlockPos> corePos() {
        return coreOffset == null ? Optional.empty() : Optional.of(worldPosition.offset(coreOffset));
    }

    public boolean belongsTo(BlockPos corePos) {
        return coreOffset != null && worldPosition.offset(coreOffset).equals(corePos);
    }

    public Optional<ChallengeBoardBlockEntity> core() {
        if (level == null || coreOffset == null) return Optional.empty();
        return level.getBlockEntity(worldPosition.offset(coreOffset)) instanceof ChallengeBoardBlockEntity core
                ? Optional.of(core)
                : Optional.empty();
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel) || coreOffset == null) return;
        BlockPos corePos = pos.offset(coreOffset);
        detach();
        if (serverLevel.getBlockState(corePos).is(CompanionContent.CHALLENGE_BOARD.get())) {
            serverLevel.destroyBlock(corePos, true);
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
        coreOffset = input.read("core_offset", BlockPos.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (coreOffset != null) {
            output.store("core_offset", BlockPos.CODEC, coreOffset);
        }
    }
}
