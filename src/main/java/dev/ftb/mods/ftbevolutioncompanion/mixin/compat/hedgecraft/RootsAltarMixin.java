package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.RootsMagic;
import elucent.rootsclassic.block.altar.AltarBlockEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AltarBlockEntity.class, remap = false)
public abstract class RootsAltarMixin {
    @WrapOperation(
            method = "activate(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;"
                    + "Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/entity/player/Player;"
                    + "Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;"
                    + "Lnet/minecraft/world/phys/BlockHitResult;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE", target = "Lelucent/rootsclassic/block/altar/AltarBlockEntity;setProgress(I)V"))
    private void ftbevo$startRitual(
            AltarBlockEntity altar, int progress, Operation<Void> original, @Local(argsOnly = true) Player player) {
        original.call(altar, RootsMagic.ritualTicks(player, progress));
        RootsMagic.awardRite(player);
    }
}
