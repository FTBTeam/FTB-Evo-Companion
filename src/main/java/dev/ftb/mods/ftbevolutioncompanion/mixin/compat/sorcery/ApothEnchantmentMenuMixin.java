package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.ftb.mods.ftbevolutioncompanion.magic.sorcery.EnchantingHooks;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ApothEnchantmentMenu.class, remap = false)
public abstract class ApothEnchantmentMenuMixin {
    @Shadow
    @Final
    protected Player player;

    @ModifyExpressionValue(
            method = "lambda$gatherStats$0(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)"
                    + "Ldev/shadowsoffire/apothic_enchanting/table/ApothEnchantmentMenu;",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ldev/shadowsoffire/apothic_enchanting/table/EnchantmentTableStats;gatherStats("
                                    + "Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)"
                                    + "Ldev/shadowsoffire/apothic_enchanting/table/EnchantmentTableStats;"))
    private EnchantmentTableStats ftbevo$addArcana(EnchantmentTableStats original) {
        return EnchantingHooks.addArcana(this.player, original);
    }

    @ModifyExpressionValue(
            method =
                    "lambda$clickMenuButton$0(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;"
                            + "IILnet/minecraft/world/item/ItemStack;ILnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ldev/shadowsoffire/apothic_enchanting/util/MiscUtil;getExpCostForSlot(II)I"))
    private int ftbevo$discountExperience(int original) {
        return this.player instanceof ServerPlayer ? EnchantingHooks.discount(this.player, original) : original;
    }
}
