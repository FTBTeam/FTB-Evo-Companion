package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.iceandfire;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.iafenvoy.iceandfire.entity.DragonBaseEntity", remap = false)
public abstract class DragonPreyInMouthMixin {
    @Inject(
            method = "updatePreyInMouth(Lnet/minecraft/world/entity/Entity;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lcom/iafenvoy/iceandfire/entity/DragonBaseEntity;getAttribute(Lnet/minecraft/core/Holder;)Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;"),
            cancellable = true)
    private void ftbevo$skipClientPreyDamage(Entity prey, CallbackInfo ci) {
        if (((Entity) (Object) this).level().isClientSide()) {
            ci.cancel();
        }
    }
}
