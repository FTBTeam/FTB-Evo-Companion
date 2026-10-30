package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.advancedae;

import com.geckolib.animatable.GeoItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase", remap = false)
public abstract class QuantumArmorStackIdMixin {
    @Inject(method = "inventoryTick", at = @At("HEAD"))
    private void ftbevo$assignGeckoLibStackId(
            ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot, CallbackInfo ci) {
        GeoItem.getOrAssignId(stack, level);
    }
}
