package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.enderio.core.common.storage.ExternalResourceStorageView;
import com.enderio.core.common.storage.ResourceStorage;

import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Pseudo
@Mixin(targets = "com.enderio.enderio.content.machines.vat.VatBlockEntity", remap = false)
public abstract class EnderIOVatFluidAccessMixin {
    @ModifyArg(
            method = "lambda$static$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/enderio/enderio/foundation/storage/SidedResourceHandler;of(Lnet/neoforged/neoforge/transfer/ResourceHandler;Lnet/minecraft/core/Direction;Lcom/enderio/enderio/api/io/IOConfigurable;)Lnet/neoforged/neoforge/transfer/ResourceHandler;"),
            index = 0)
    private static ResourceHandler<FluidResource> ftbevo$enforceTankAccess(ResourceHandler<FluidResource> storage) {
        // Restrict automation without changing the storage used by recipe tasks.
        return new ExternalResourceStorageView<>((ResourceStorage<FluidResource>) storage);
    }
}
