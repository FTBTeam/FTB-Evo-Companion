package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.buuz135.functionalstorage.client.loader.FramedModel", remap = false)
public abstract class FunctionalStorageFramedModelMixin {
    @WrapOperation(
            method = "wrapModels",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;replaceAll(Ljava/util/function/BiFunction;)V"))
    private static void ftbevo$wrapFramedStatesOnly(
            Map<BlockState, BlockStateModel> models,
            BiFunction<BlockState, BlockStateModel, BlockStateModel> wrapper,
            Operation<Void> original,
            @Local(argsOnly = true) ModelEvent.ModifyBakingResult event) {
        BlockStateModel probe = event.getBakingResult().missingModels().block();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (wrapper.apply(block.defaultBlockState(), probe) == probe) {
                continue;
            }
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                BlockStateModel model = models.get(state);
                if (model != null) {
                    models.put(state, wrapper.apply(state, model));
                }
            }
        }
    }
}
