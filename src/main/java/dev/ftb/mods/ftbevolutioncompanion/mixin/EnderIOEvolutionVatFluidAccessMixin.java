package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.enderio.core.common.storage.ExternalResourceStorageView;
import com.enderio.core.common.storage.ResourceStorage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.sakurain.enderio_evolution.EnderIOEvolution", remap = false)
public abstract class EnderIOEvolutionVatFluidAccessMixin {
    @Inject(method = "lambda$registerCapabilities$10", at = @At("RETURN"), cancellable = true)
    private static void ftbevo$enforceTankAccess(CallbackInfoReturnable<ResourceHandler<FluidResource>> cir) {
        ResourceHandler<FluidResource> storage = cir.getReturnValue();
        if (storage != null) {
            cir.setReturnValue(new ExternalResourceStorageView<>((ResourceStorage<FluidResource>) storage));
        }
    }
}
