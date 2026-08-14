package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SwordBlock;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Item.class)
public abstract class ItemMixin {
    @ModifyExpressionValue(
            method = "use(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;has(Lnet/minecraft/core/component/DataComponentType;)Z"))
    private boolean ftbevo$swordBlockUse(boolean original, Level level, Player player, InteractionHand hand) {
        return original || SwordBlock.canBlock(player, player.getItemInHand(hand));
    }

    @ModifyReturnValue(method = "getUseDuration", at = @At("RETURN"))
    private int ftbevo$swordBlockUseDuration(int original, ItemStack stack, LivingEntity user) {
        if (original == 0 && SwordBlock.canBlock(user, stack)) {
            return 72000;
        }
        return original;
    }

    @ModifyReturnValue(method = "getUseAnimation", at = @At("RETURN"))
    private ItemUseAnimation ftbevo$swordBlockAnimation(ItemUseAnimation original, ItemStack stack) {
        if (original == ItemUseAnimation.NONE && SkillsHelper.isSword(stack)) {
            return ItemUseAnimation.BLOCK;
        }
        return original;
    }
}
