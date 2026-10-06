package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hedgecraft.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client.RootsMagicClient;
import elucent.rootsclassic.datacomponent.SpellData;
import elucent.rootsclassic.item.CrystalStaffItem;
import elucent.rootsclassic.item.StaffItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
        value = {StaffItem.class, CrystalStaffItem.class},
        remap = false)
public abstract class RootsStaffTooltipMixin {
    @WrapOperation(
            method =
                    "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/Item$TooltipContext;"
                            + "Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;"
                            + "Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At(value = "INVOKE", target = "Lelucent/rootsclassic/datacomponent/SpellData;potency()I"))
    private int ftbevo$showSkillPotency(SpellData spell, Operation<Integer> original) {
        int staffBonus = (Object) this instanceof CrystalStaffItem ? 1 : 0;
        return RootsMagicClient.staffPotency(spell, original.call(spell), staffBonus);
    }
}
