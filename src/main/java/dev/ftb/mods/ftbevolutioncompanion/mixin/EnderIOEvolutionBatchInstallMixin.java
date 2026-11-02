package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.sakurain.enderio_evolution.event.BatchInstallInteractionHandler", remap = false)
public abstract class EnderIOEvolutionBatchInstallMixin {
    @Inject(method = "canMachineAcceptItem", at = @At("HEAD"), cancellable = true)
    private static void ftbevo$onlyEnderIOMachines(
            Level level, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!(level.getBlockEntity(pos) instanceof MachineBlockEntity)) {
            cir.setReturnValue(false);
        }
    }
}
