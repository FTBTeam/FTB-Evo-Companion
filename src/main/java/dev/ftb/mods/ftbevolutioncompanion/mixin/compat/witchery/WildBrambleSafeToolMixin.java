package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.witchery;

import dev.sterner.witchery.content.block.bramble.WildBrambleBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WildBrambleBlock.class, remap = false)
public abstract class WildBrambleSafeToolMixin {
    @Unique
    private static final TagKey<Item> FTBEVO$GOLD_TOOLS =
            TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("witchery", "gold_tools"));

    @Inject(method = "isSafeHarvestTool(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void ftbevo$anyGoldTool(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() && stack.is(FTBEVO$GOLD_TOOLS)) {
            cir.setReturnValue(true);
        }
    }
}
