package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.apothic;

import dev.shadowsoffire.apotheosis.loot.LootRarity;

import ianm1647.apothic_compats.affix.irons_artifice.BulletModifierAffix;

import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BulletModifierAffix.class, remap = false)
public interface BulletModifierAffixAccessor {
    @Accessor("target")
    BulletModifierAffix.Target ftbevo$target();

    @Invoker("applyEffect")
    void ftbevo$applyEffect(LivingEntity target, LootRarity rarity, float level);
}
