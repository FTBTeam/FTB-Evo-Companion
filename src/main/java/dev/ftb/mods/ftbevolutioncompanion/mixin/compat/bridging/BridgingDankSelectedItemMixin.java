package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.bridging;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me.cg360.mod.bridging.compat.handler.DankStorageHandler", remap = false)
public abstract class BridgingDankSelectedItemMixin {
    @Unique
    private static MethodHandle ftbevo$selected;

    @Unique
    private static boolean ftbevo$missing;

    @Redirect(
            method = {"canBePlaced", "canBePlacedInWorld", "generatePlacementTarget"},
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Ltfar/dankstorage/item/DankItem;getSelectedItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack ftbevo$selectedItem(ItemStack dank) {
        if (ftbevo$selected == null && !ftbevo$missing) {
            try {
                ftbevo$selected = MethodHandles.publicLookup()
                        .findStatic(
                                Class.forName("tfar.dankstorage.item.DankItem"),
                                "getTotalSelectedItem",
                                MethodType.methodType(ItemStack.class, ItemStack.class));
            } catch (ReflectiveOperationException e) {
                ftbevo$missing = true;
            }
        }
        if (ftbevo$selected == null) {
            return ItemStack.EMPTY;
        }
        try {
            return (ItemStack) ftbevo$selected.invokeExact(dank);
        } catch (Throwable e) {
            return ItemStack.EMPTY;
        }
    }
}
