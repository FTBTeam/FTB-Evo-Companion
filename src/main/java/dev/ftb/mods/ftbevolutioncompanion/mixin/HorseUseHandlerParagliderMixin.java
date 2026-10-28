package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.simplywinged.ParagliderWings;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "in2bubble.simplywinged.HorseUseHandler", remap = false)
public abstract class HorseUseHandlerParagliderMixin {
    @WrapOperation(
            method = "handleUseEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;is(Ljava/lang/Object;)Z"))
    private static boolean ftbevo$paragliderInsteadOfElytra(ItemStack stack, Object item, Operation<Boolean> original) {
        return ParagliderWings.swapElytraCheck(stack, item, original);
    }

    @WrapOperation(
            method = "onEntityInteract",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"),
            require = 0)
    private static void ftbevo$silenceInteractLog(
            Logger logger, String message, Object[] args, Operation<Void> original) {}

    @WrapOperation(
            method = "onEntityInteract",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V"),
            require = 0)
    private static void ftbevo$silenceHandledLog(Logger logger, String message, Object arg, Operation<Void> original) {}
}
