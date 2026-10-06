package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus.HephaestusTools;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.titammods.hephaestus_tools.tools.item.ModifiableItem", remap = false)
public abstract class HephaestusToolHarvestMixin {
    @Inject(
            method =
                    "isCorrectToolForDrops(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("RETURN"),
            cancellable = true)
    private void ftbevo$respectIncorrectTags(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && state.is(HephaestusTools.incorrectFor(stack))) {
            cir.setReturnValue(false);
        }
    }
}
