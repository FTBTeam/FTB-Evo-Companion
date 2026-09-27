package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.RootsMagic;

import elucent.rootsclassic.component.ComponentBase;
import elucent.rootsclassic.component.EnumCastType;
import elucent.rootsclassic.item.CrystalStaffItem;
import elucent.rootsclassic.item.StaffItem;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {StaffItem.class, CrystalStaffItem.class}, remap = false)
public abstract class RootsStaffCastMixin {
    @ModifyExpressionValue(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;"
                    + "Lnet/minecraft/world/entity/LivingEntity;I)Z",
            at = @At(value = "INVOKE", target = "Lelucent/rootsclassic/component/ComponentBase;getManaCost()F"))
    private float ftbevo$reduceManaCost(float cost, @Local(argsOnly = true) LivingEntity caster) {
        return RootsMagic.manaCost(caster, cost);
    }

    @WrapOperation(
            method = "releaseUsing(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;"
                    + "Lnet/minecraft/world/entity/LivingEntity;I)Z",
            at = @At(value = "INVOKE",
                    target = "Lelucent/rootsclassic/component/ComponentBase;doEffect(Lnet/minecraft/world/level/Level;"
                            + "Lnet/minecraft/world/entity/Entity;Lelucent/rootsclassic/component/EnumCastType;DDDDDD)V"))
    private void ftbevo$castWithPotency(ComponentBase component, Level level, Entity caster, EnumCastType type,
                                        double x, double y, double z, double potency, double efficiency, double size,
                                        Operation<Void> original) {
        double boosted = potency + RootsMagic.potencyBonus(component, caster, (int) potency);
        original.call(component, level, caster, type, x, y, z, boosted, efficiency, size);
        RootsMagic.awardCast(caster);
    }

    @WrapOperation(
            method = "onUseTick(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;"
                    + "Lnet/minecraft/world/item/ItemStack;I)V",
            at = @At(value = "INVOKE",
                    target = "Lelucent/rootsclassic/component/ComponentBase;castingAction(Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private void ftbevo$channelWithPotency(ComponentBase component, Player player, int count, int potency,
                                           int efficiency, int size, Operation<Void> original) {
        original.call(component, player, count, potency + RootsMagic.potencyBonus(component, player, potency),
                efficiency, size);
    }
}
