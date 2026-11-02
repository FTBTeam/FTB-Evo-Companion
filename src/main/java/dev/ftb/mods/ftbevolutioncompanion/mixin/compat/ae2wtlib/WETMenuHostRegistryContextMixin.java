package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2wtlib;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.mari_023.ae2wtlib.api.terminal.WTMenuHost;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "de.mari_023.ae2wtlib.wet.WETMenuHost", remap = false)
public abstract class WETMenuHostRegistryContextMixin {
    @WrapOperation(
            method = "markForSave",
            require = 0,
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/level/storage/TagValueOutput;"
                                    + "createWithoutContext(Lnet/minecraft/util/ProblemReporter;)"
                                    + "Lnet/minecraft/world/level/storage/TagValueOutput;"))
    private TagValueOutput ftbevo$saveWithRegistries(ProblemReporter reporter, Operation<TagValueOutput> original) {
        WTMenuHost host = (WTMenuHost) (Object) this;
        if (host.getPlayer() == null) {
            return original.call(reporter);
        }
        return TagValueOutput.createWithContext(reporter, host.getPlayer().registryAccess());
    }
}
