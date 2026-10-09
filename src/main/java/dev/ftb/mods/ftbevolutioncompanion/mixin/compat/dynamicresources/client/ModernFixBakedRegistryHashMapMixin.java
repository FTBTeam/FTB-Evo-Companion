package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.ftb.mods.ftbevolutioncompanion.compat.dynamicresources.ForwardingHashMap;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "org.embeddedt.modernfix.dynresources.DynamicModelSystem", remap = false)
public abstract class ModernFixBakedRegistryHashMapMixin {
    @ModifyReturnValue(method = "createDynamicBakedRegistry", at = @At("RETURN"))
    private static Map<Object, Object> ftbevo$exposeAsHashMap(Map<Object, Object> original) {
        return new ForwardingHashMap<>(original);
    }
}
