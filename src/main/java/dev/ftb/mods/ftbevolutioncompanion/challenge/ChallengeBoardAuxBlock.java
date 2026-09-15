package dev.ftb.mods.ftbevolutioncompanion.challenge;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ChallengeBoardAuxBlock extends ChallengeBoardBlock {
    public static final MapCodec<ChallengeBoardAuxBlock> AUX_CODEC = simpleCodec(ChallengeBoardAuxBlock::new);

    public ChallengeBoardAuxBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends ChallengeBoardBlock> codec() {
        return AUX_CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ChallengeBoardAuxBlockEntity(pos, state);
    }
}
