package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "com.enderio.enderio.content.filters.item.limited.LimitedItemFilter", remap = false)
public abstract class EnderIOLimitedFilterCountMixin {
    @Shadow
    @Final
    private NonNullList<ItemStack> matches;

    @Inject(
            method = {
                "<init>(Ljava/util/List;ZLcom/enderio/enderio/content/filters/item/general/DamageFilterMode;)V",
                "<init>(Lnet/minecraft/core/NonNullList;ZLcom/enderio/enderio/content/filters/item/general/DamageFilterMode;)V"
            },
            at = @At("RETURN"))
    private void ftbevo$clampCounts(CallbackInfo ci) {
        for (int i = 0; i < matches.size(); i++) {
            ItemStack stack = matches.get(i);
            if (stack.getCount() > Item.ABSOLUTE_MAX_STACK_SIZE) {
                matches.set(i, stack.copyWithCount(Item.ABSOLUTE_MAX_STACK_SIZE));
            }
        }
    }
}
