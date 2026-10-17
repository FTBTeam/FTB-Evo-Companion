package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.titanium;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
        targets = {
            "com.hrznstudio.titanium.container.addon.UpdatableSlotItemHandler",
            "com.hrznstudio.titanium.container.impl.DisableableItemHandlerSlot"
        },
        remap = false)
public abstract class TitaniumSlotFilterMixin {
    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void ftbevo$respectInventoryFilter(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ()) {
            return;
        }

        ResourceHandlerSlot slot = (ResourceHandlerSlot) (Object) this;
        if (!slot.getResourceHandler().isValid(slot.getSlotIndex(), ItemResource.of(stack))) {
            cir.setReturnValue(false);
        }
    }
}
