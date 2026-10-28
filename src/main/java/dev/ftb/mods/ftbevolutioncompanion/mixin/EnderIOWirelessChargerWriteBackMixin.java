package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.enderio.enderio.content.machines.wireless_charger.WirelessChargerBlockEntity", remap = false)
public abstract class EnderIOWirelessChargerWriteBackMixin {
    @WrapOperation(
            method = "chargeItem",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/neoforged/neoforge/transfer/access/ItemAccess;forStack(Lnet/minecraft/world/item/ItemStack;)Lnet/neoforged/neoforge/transfer/access/ItemAccess;"))
    private ItemAccess ftbevo$captureAccess(
            ItemStack stack,
            Operation<ItemAccess> original,
            @Share("accesses") LocalRef<Map<ItemStack, ItemAccess>> accesses) {
        ItemAccess created = original.call(stack);
        if (accesses.get() == null) {
            accesses.set(new IdentityHashMap<>());
        }
        accesses.get().put(stack, created);
        return created;
    }

    @Inject(method = "chargeItem", at = @At("RETURN"))
    private void ftbevo$writeBack(CallbackInfo ci, @Share("accesses") LocalRef<Map<ItemStack, ItemAccess>> accesses) {
        Map<ItemStack, ItemAccess> created = accesses.get();
        if (created == null) {
            return;
        }
        created.forEach((stack, access) -> {
            ItemResource resource = access.getResource();
            if (!stack.isEmpty()
                    && !resource.isEmpty()
                    && resource.is(stack.getItem())
                    && !resource.getComponentsPatch().equals(stack.getComponentsPatch())) {
                stack.applyComponents(resource.getComponentsPatch());
            }
        });
    }
}
