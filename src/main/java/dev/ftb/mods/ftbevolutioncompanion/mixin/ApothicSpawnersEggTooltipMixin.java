package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;

import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "dev.shadowsoffire.apothic_spawners.ASEvents", remap = false)
public abstract class ApothicSpawnersEggTooltipMixin {
    @Inject(method = "handleTooltips", at = @At("HEAD"), cancellable = true)
    private void ftbevo$skipUntypedEggs(ItemTooltipEvent event, CallbackInfo ci) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof SpawnEggItem && SpawnEggItem.getType(stack) == null) {
            ci.cancel();
        }
    }
}
