package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2;

import dev.ftb.mods.ftbevolutioncompanion.compat.curios.CurioSlotStacks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "appeng.menu.locator.CuriosItemLocator", remap = false)
public abstract class CuriosItemLocatorMixin {
    @Shadow
    @Final
    private int curioSlot;

    @Inject(method = "locateItem", at = @At("HEAD"), cancellable = true)
    private void ftbevo$liveCurioStack(Player player, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack stack = CurioSlotStacks.liveStack(player, curioSlot);
        if (stack != null) {
            cir.setReturnValue(stack);
        }
    }
}
