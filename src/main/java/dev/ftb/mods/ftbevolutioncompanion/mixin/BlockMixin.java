package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @ModifyVariable(
            method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            argsOnly = true)
    private static ItemStack ftbevo$miningFortune(ItemStack tool, BlockState state, Level level, BlockPos pos,
                                                  BlockEntity blockEntity, Entity breaker) {
        if (!(breaker instanceof Player player) || tool.isEmpty()) {
            return tool;
        }
        int bonus = (int) SkillsHelper.attr(player, SkillsRegistry.MINING_FORTUNE);
        if (bonus <= 0) {
            return tool;
        }
        Holder<Enchantment> fortune = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        ItemStack boosted = tool.copy();
        boosted.enchant(fortune, boosted.getEnchantmentLevel(fortune) + bonus);
        return boosted;
    }
}
