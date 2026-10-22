package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.dynamicresources.client;

import dev.ftb.mods.ftbevolutioncompanion.compat.dynamicresources.ForwardingHashMap;
import java.util.Map;
import net.neoforged.neoforge.client.event.ModelEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "io.github.chiselteam.ctm.impl.model.CTMQuadDeduplicator", remap = false)
public abstract class CtmQuadDeduplicatorMixin {
    @Inject(method = "onModifyBakingResult", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$skipLazyModelMap(ModelEvent.ModifyBakingResult event, CallbackInfo ci) {
        Map<?, ?> models = event.getBakingResult().blockStateModels();
        if (models instanceof ForwardingHashMap<?, ?>
                || models.getClass().getName().startsWith("org.embeddedt.modernfix.")) {
            ci.cancel();
        }
    }
}
