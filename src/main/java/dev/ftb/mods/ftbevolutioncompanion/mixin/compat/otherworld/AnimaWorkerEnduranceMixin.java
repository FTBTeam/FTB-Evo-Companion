package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.otherworld;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.anima.MachineBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.magic.otherworld.OtherworldMagic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MachineBlockEntity.class, remap = false)
public abstract class AnimaWorkerEnduranceMixin {
    @Shadow
    private String owner;

    @ModifyReturnValue(method = "durability(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"))
    private int ftbevo$workerEndurance(int durability) {
        return OtherworldMagic.enduringDurability((MachineBlockEntity) (Object) this, owner, durability);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void ftbevo$refreshWorkerSnapshot(CallbackInfo ci) {
        OtherworldMagic.refreshWorkerSnapshot((MachineBlockEntity) (Object) this, owner);
    }
}
