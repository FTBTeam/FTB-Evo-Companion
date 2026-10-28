package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.enderio.enderio.content.machines.wired_charger.WiredChargerBlockEntity", remap = false)
public abstract class EnderIOWiredChargerWriteBackMixin {
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
            @Share("access") LocalRef<ItemAccess> access,
            @Share("stack") LocalRef<ItemStack> charged) {
        ItemAccess created = original.call(stack);
        access.set(created);
        charged.set(stack);
        return created;
    }

    @Inject(method = "chargeItem", at = @At("RETURN"))
    private void ftbevo$writeBack(
            CallbackInfo ci,
            @Share("access") LocalRef<ItemAccess> access,
            @Share("stack") LocalRef<ItemStack> charged) {
        ItemAccess created = access.get();
        ItemStack stack = charged.get();
        if (created == null || stack == null || stack.isEmpty()) {
            return;
        }
        ItemResource resource = created.getResource();
        if (resource.isEmpty() || !resource.is(stack.getItem())) {
            return;
        }
        if (!resource.getComponentsPatch().equals(stack.getComponentsPatch())) {
            stack.applyComponents(resource.getComponentsPatch());
            ((BlockEntity) (Object) this).setChanged();
        }
    }
}
