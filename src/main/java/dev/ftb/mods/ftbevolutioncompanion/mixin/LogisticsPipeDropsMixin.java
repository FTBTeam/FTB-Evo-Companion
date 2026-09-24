package dev.ftb.mods.ftbevolutioncompanion.mixin;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "logisticspipes.pipes.basic.LogisticsBlockGenericPipe", remap = false)
public abstract class LogisticsPipeDropsMixin extends Block {
    protected LogisticsPipeDropsMixin(Properties properties) {
        super(properties);
    }

    @Shadow
    public abstract NonNullList<ItemStack> getDrops(BlockGetter level, BlockPos pos, BlockState state, int fortune);

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        Vec3 origin = params.getOptionalParameter(LootContextParams.ORIGIN);
        if (origin == null) {
            return List.of();
        }
        return getDrops(params.getLevel(), BlockPos.containing(origin), state, 0);
    }
}
