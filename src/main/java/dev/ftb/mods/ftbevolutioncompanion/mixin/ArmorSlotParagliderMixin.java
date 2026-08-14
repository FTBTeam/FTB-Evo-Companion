package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.simplywinged.ParagliderWings;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotParagliderMixin {
    @Shadow
    @Final
    private LivingEntity owner;

    @Shadow
    @Final
    private EquipmentSlot slot;

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void ftbevo$allowHorseBodyParaglider(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() || this.slot != EquipmentSlot.BODY || !(this.owner instanceof AbstractHorse)) {
            return;
        }

        if (ParagliderWings.isWing(stack)) {
            cir.setReturnValue(true);
        }
    }
}
