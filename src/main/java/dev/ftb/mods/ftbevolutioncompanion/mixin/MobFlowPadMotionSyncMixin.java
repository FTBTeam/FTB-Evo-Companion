package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.misterd.mobflowutilities.blockentity.custom.FlowPadBlockEntity", remap = false)
public abstract class MobFlowPadMotionSyncMixin {
    @WrapOperation(
            method = "applyMovementToEntity",
            at =
                    @At(
                            value = "FIELD",
                            target = "Lnet/minecraft/world/entity/Entity;hurtMarked:Z",
                            opcode = Opcodes.PUTFIELD))
    private void ftbevo$markOnlyPlayers(Entity entity, boolean value, Operation<Void> original) {
        if (entity instanceof Player) {
            original.call(entity, value);
        }
    }
}
