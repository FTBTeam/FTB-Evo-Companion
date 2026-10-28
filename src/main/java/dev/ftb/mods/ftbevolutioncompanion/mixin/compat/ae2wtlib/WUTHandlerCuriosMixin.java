package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ae2wtlib;

import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;
import de.mari_023.ae2wtlib.api.AE2wtlibAPI;
import de.mari_023.ae2wtlib.api.registration.WTDefinition;
import de.mari_023.ae2wtlib.api.terminal.WUTHandler;
import dev.ftb.mods.ftbevolutioncompanion.compat.curios.CurioSlotStacks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WUTHandler.class, remap = false)
public abstract class WUTHandlerCuriosMixin {
    @Inject(method = "findTerminal", at = @At("RETURN"), cancellable = true)
    private static void ftbevo$findInCurios(
            Player player, WTDefinition terminal, CallbackInfoReturnable<ItemMenuHostLocator> cir) {
        if (cir.getReturnValue() != null) {
            return;
        }
        ItemMenuHostLocator found = null;
        int slots = CurioSlotStacks.slotCount(player);
        for (int slot = 0; slot < slots; slot++) {
            ItemStack stack = CurioSlotStacks.liveStack(player, slot);
            if (stack == null || !WUTHandler.hasTerminal(stack, terminal)) {
                continue;
            }
            if (AE2wtlibAPI.isUniversalTerminal(stack)) {
                cir.setReturnValue(MenuLocators.forCurioSlot(slot));
                return;
            }
            if (found == null) {
                found = MenuLocators.forCurioSlot(slot);
            }
        }
        if (found != null) {
            cir.setReturnValue(found);
        }
    }
}
