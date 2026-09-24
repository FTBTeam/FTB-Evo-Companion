package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SkylinePartBlockEntity extends BlockEntity {
    @Nullable
    private BlockPos coreOffset;
    @Nullable
    private VoxelShape cachedShape;

    public SkylinePartBlockEntity(BlockPos pos, BlockState state) {
        super(PyramidRegistry.SKYLINE_PART.get(), pos, state);
    }

    public void setCore(BlockPos corePos) {
        coreOffset = corePos.subtract(worldPosition);
        cachedShape = null;
        setChanged();
    }

    public void detach() {
        coreOffset = null;
        cachedShape = null;
    }

    public boolean belongsTo(BlockPos corePos) {
        return coreOffset != null && worldPosition.offset(coreOffset).equals(corePos);
    }

    public Optional<BlockPos> corePos() {
        return coreOffset == null ? Optional.empty() : Optional.of(worldPosition.offset(coreOffset));
    }

    public Optional<SkylineBlockEntity> core() {
        if (level == null || coreOffset == null) return Optional.empty();
        BlockPos corePos = worldPosition.offset(coreOffset);
        if (!level.isLoaded(corePos)) return Optional.empty();
        return level.getBlockEntity(corePos) instanceof SkylineBlockEntity core ? Optional.of(core) : Optional.empty();
    }

    public PyramidLayout.Bay bay() {
        return modelOffset().map(PyramidLayout::bay).orElse(PyramidLayout.Bay.NONE);
    }

    public VoxelShape shape() {
        if (cachedShape != null) return cachedShape;
        Optional<Direction> facing = coreFacing();
        Optional<Vec3i> model = modelOffset();
        if (facing.isEmpty() || model.isEmpty()) return Shapes.block();
        cachedShape = PyramidLayout.shape(model.get(), facing.get());
        return cachedShape;
    }

    private Optional<Direction> coreFacing() {
        if (level == null || coreOffset == null) return Optional.empty();
        BlockPos corePos = worldPosition.offset(coreOffset);
        if (!level.isLoaded(corePos)) return Optional.empty();
        BlockState coreState = level.getBlockState(corePos);
        return coreState.hasProperty(SkylineBlock.FACING) ? Optional.of(coreState.getValue(SkylineBlock.FACING)) : Optional.empty();
    }

    private Optional<Vec3i> modelOffset() {
        if (coreOffset == null) return Optional.empty();
        Vec3i world = new Vec3i(-coreOffset.getX(), -coreOffset.getY(), -coreOffset.getZ());
        return coreFacing().map(facing -> PyramidLayout.toModel(world, facing));
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (!(level instanceof ServerLevel serverLevel) || coreOffset == null) return;
        BlockPos corePos = pos.offset(coreOffset);
        detach();
        if (serverLevel.getBlockState(corePos).is(CompanionContent.SKYLINE.get())) {
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
        cachedShape = null;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (coreOffset != null) {
            output.store("core_offset", BlockPos.CODEC, coreOffset);
        }
    }
}
