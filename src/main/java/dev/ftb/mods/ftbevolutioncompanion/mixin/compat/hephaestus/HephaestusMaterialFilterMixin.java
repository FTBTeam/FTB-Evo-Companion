package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.HephaestusTools;
import java.util.Map;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.titammods.hephaestus_tools.materials.MaterialManager", remap = false)
public abstract class HephaestusMaterialFilterMixin {
    @Inject(method = "replaceAll(Ljava/util/Map;)V", at = @At("HEAD"))
    private void ftbevo$dropRemovedMaterials(Map<Object, Object> materials, CallbackInfo ci) {
        materials.keySet().removeIf(key -> {
            Identifier id = HephaestusTools.materialId(key);
            return id != null && HephaestusTools.REMOVED_MATERIALS.contains(id.getPath());
        });
    }
}
