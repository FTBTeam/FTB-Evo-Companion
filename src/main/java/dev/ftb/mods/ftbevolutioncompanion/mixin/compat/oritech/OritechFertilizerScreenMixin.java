package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.oritech;

import java.util.function.Supplier;
import net.minecraft.world.inventory.MenuType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "rearth.oritech.block.entity.interaction.FertilizerBlockEntity", remap = false)
public abstract class OritechFertilizerScreenMixin {
    @Unique
    private static Supplier<?> ftbevo$fertilizerScreen;

    @Unique
    private static boolean ftbevo$missing;

    @Inject(
            method = "getScreenHandlerType()Lnet/minecraft/world/inventory/MenuType;",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$useFertilizerScreen(CallbackInfoReturnable<MenuType<?>> cir) {
        if (ftbevo$fertilizerScreen == null && !ftbevo$missing) {
            try {
                ftbevo$fertilizerScreen = (Supplier<?>) Class.forName("rearth.oritech.client.init.ModScreens")
                        .getField("FERTILIZER_SCREEN")
                        .get(null);
            } catch (ReflectiveOperationException | ClassCastException e) {
                ftbevo$missing = true;
            }
        }
        if (ftbevo$fertilizerScreen != null && ftbevo$fertilizerScreen.get() instanceof MenuType<?> menu) {
            cir.setReturnValue(menu);
        }
    }
}
