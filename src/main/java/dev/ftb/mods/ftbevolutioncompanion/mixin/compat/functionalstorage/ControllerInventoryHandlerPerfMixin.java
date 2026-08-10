package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.functionalstorage;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

@Mixin(targets = "com.buuz135.functionalstorage.inventory.ControllerInventoryHandler", remap = false)
public abstract class ControllerInventoryHandlerPerfMixin {
    @Unique
    private List<?> ftbevolutioncompanion$cachedHandlers;
    @Unique
    private int ftbevolutioncompanion$cachedSize;
    @Unique
    private Set<Object> ftbevolutioncompanion$membership;

    @WrapOperation(
            method = {"getStackInSlot", "insertItem", "extractItem"},
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z")
    )
    private boolean ftbevolutioncompanion$fastHandlerContains(List<?> handlers, Object handler, Operation<Boolean> original) {
        if (handlers != ftbevolutioncompanion$cachedHandlers || handlers.size() != ftbevolutioncompanion$cachedSize) {
            Set<Object> rebuilt = Collections.newSetFromMap(new IdentityHashMap<>());
            rebuilt.addAll(handlers);
            ftbevolutioncompanion$membership = rebuilt;
            ftbevolutioncompanion$cachedHandlers = handlers;
            ftbevolutioncompanion$cachedSize = handlers.size();
        }
        return ftbevolutioncompanion$membership.contains(handler);
    }
}
