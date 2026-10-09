package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.unfamily.colossal_reactors.client.turbine.TurbineRotorClientRegistration", remap = false)
public abstract class ColossalReactorsTurbineModelMixin {
    @Unique
    private static final Set<Identifier> ftbevo$TURBINE_BLOCKS = Set.of(
            Identifier.fromNamespaceAndPath("colossal_reactors", "turbine_rod"),
            Identifier.fromNamespaceAndPath("colossal_reactors", "turbine_blade"));

    @WrapOperation(
            method = "onModifyBakingResult",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object ftbevo$getTurbineModelsOnly(Map<?, ?> models, Object key, Operation<Object> original) {
        if (key instanceof BlockState state
                && ftbevo$TURBINE_BLOCKS.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()))) {
            return original.call(models, key);
        }
        return null;
    }
}
