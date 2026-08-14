package dev.ftb.mods.ftbevolutioncompanion.skills;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlocksAttacks;

import java.util.List;
import java.util.Optional;

public final class SwordBlock {
    private SwordBlock() {
    }

    public static boolean canBlock(LivingEntity entity, ItemStack stack) {
        return entity instanceof Player player && SkillsHelper.isSword(stack)
                && SkillsHelper.attr(player, SkillsRegistry.SWORD_BLOCK) > 0.0;
    }

    public static BlocksAttacks component(LivingEntity entity) {
        float factor = (float) SkillsHelper.attr(entity, SkillsRegistry.SWORD_BLOCK);
        Optional<HolderSet<DamageType>> bypassed = entity.level().registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE)
                .get(DamageTypeTags.BYPASSES_SHIELD)
                .map(set -> (HolderSet<DamageType>) set);
        return new BlocksAttacks(0.25F, 1.0F,
                List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, factor)),
                BlocksAttacks.ItemDamageFunction.DEFAULT,
                bypassed, Optional.empty(), Optional.empty());
    }
}
