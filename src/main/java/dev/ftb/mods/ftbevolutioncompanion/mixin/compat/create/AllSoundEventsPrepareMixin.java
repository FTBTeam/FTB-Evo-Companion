package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

@Mixin(targets = "com.simibubi.create.AllSoundEvents", remap = false)
public class AllSoundEventsPrepareMixin {
    @Redirect(
            method = "prepare",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;values()Ljava/util/Collection;")
    )
    private static Collection<?> ftbevolutioncompanion$snapshotSoundEntries(Map<?, ?> all) {
        List<Object> snapshot = new ArrayList<>();
        for (int attempt = 0; attempt < 256; attempt++) {
            snapshot = new ArrayList<>();
            try {
                for (Object entry : all.values()) {
                    snapshot.add(entry);
                }
                return snapshot;
            } catch (ConcurrentModificationException | ArrayIndexOutOfBoundsException race) {
                Thread.onSpinWait();
            }
        }
        return snapshot;
    }
}
