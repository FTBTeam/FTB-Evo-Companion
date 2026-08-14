package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import dev.ftb.mods.ftbevolutioncompanion.athletics.AthleticsAbilities;
import dev.ftb.mods.ftbevolutioncompanion.skills.SwordBlock;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void ftbevo$athleticsWallCling(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player
                && !player.level().isClientSide()
                && AthleticsAbilities.isWallClinging(player)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyExpressionValue(
            method = {"getItemBlockingWith", "applyItemBlocking"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object ftbevo$swordBlocksAttacks(Object original) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (original == null && SwordBlock.canBlock(self, self.getUseItem())) {
            return SwordBlock.component(self);
        }
        return original;
    }
}
