package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.immersiveengineering;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "blusunrize.immersiveengineering.common.util.loot.LootBlockStateFromLocationPredicate", remap = false)
public abstract class LootBlockStatePredicateChunkLoadMixin {

    @WrapOperation(
            method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
            )
    )
    private BlockState ftbevolutioncompanion$readWithoutForcingChunkLoad(ServerLevel level, BlockPos pos, Operation<BlockState> original) {
        MinecraftServer server = level.getServer();
        if (server != null && server.isSameThread()) {
            return original.call(level, pos);
        }
        LevelChunk chunk = level.getChunkSource().getChunkNow(
                SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()));
        return chunk == null ? Blocks.AIR.defaultBlockState() : chunk.getBlockState(pos);
    }
}
