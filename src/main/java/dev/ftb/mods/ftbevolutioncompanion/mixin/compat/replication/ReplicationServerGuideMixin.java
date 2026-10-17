package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.replication;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.buuz135.replication.Replication", remap = false)
public abstract class ReplicationServerGuideMixin {
    @WrapOperation(
            method = "initModules",
            at = @At(value = "INVOKE", target = "Lnet/neoforged/fml/ModList;isLoaded(Ljava/lang/String;)Z"))
    private boolean ftbevo$skipGuideOnServer(ModList modList, String modId, Operation<Boolean> original) {
        if ("guideme".equals(modId) && !FMLEnvironment.getDist().isClient()) {
            return false;
        }
        return original.call(modList, modId);
    }
}
