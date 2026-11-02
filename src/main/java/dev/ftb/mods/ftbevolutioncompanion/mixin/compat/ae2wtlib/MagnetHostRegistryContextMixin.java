package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2wtlib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "de.mari_023.ae2wtlib.wct.magnet_card.MagnetHost", remap = false)
public abstract class MagnetHostRegistryContextMixin {
    @WrapOperation(
            method = {"updatePickupFilter", "updateInsertFilter", "switchInsertPickup"},
            require = 0,
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/storage/TagValueOutput;"
                                    + "createWithoutContext(Lnet/minecraft/util/ProblemReporter;)"
                                    + "Lnet/minecraft/world/level/storage/TagValueOutput;"))
    private TagValueOutput ftbevo$saveWithRegistries(ProblemReporter reporter, Operation<TagValueOutput> original) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return original.call(reporter);
        }
        return TagValueOutput.createWithContext(reporter, server.registryAccess());
    }
}
