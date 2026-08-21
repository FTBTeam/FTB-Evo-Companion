package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.klikli_dev.occultism.common.item.tool.GuideBookItem", remap = false)
public abstract class OccultismGuideBookRemainderMixin {
    @Inject(method = "getCraftingRemainder(Lnet/minecraft/world/item/ItemInstance;)"
            + "Lnet/minecraft/world/item/ItemStackTemplate;",
            at = @At("HEAD"), cancellable = true)
    private void ftbevo$keepAllComponents(ItemInstance instance, CallbackInfoReturnable<ItemStackTemplate> cir) {
        if (instance instanceof ItemStack stack) {
            cir.setReturnValue(new ItemStackTemplate((Item) (Object) this, stack.getComponentsPatch()));
        }
    }
}
