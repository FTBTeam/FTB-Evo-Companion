package dev.ftb.mods.ftbevolutioncompanion.gunnery;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import io.redspace.irons_artifice.api.ComposeShotEvent;
import io.redspace.irons_artifice.api.GunShootEvent;
import io.redspace.irons_artifice.data.ComponentType;
import io.redspace.irons_artifice.data.ShotComponents;
import io.redspace.irons_artifice.data.Value;
import io.redspace.irons_artifice.data.ValueModifier;
import io.redspace.irons_artifice.gun.ShotProfile;
import io.redspace.irons_artifice.item.TricorneItem;
import io.redspace.irons_artifice.registry.ItemRegistry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

public final class GunneryHooks {
    private static final TagKey<DamageType> ATLANTEAN_RIFLE =
            TagKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("nautec", "atlantean_rifle"));

    private GunneryHooks() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.HIGH, GunneryHooks::onComposeShot);
        NeoForge.EVENT_BUS.addListener(GunneryHooks::onGunShootPre);
        NeoForge.EVENT_BUS.addListener(GunneryHooks::onRifleDamage);
        NeoForge.EVENT_BUS.addListener(GunneryHooks::onRifleHit);
    }

    public static void onRifleDamage(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(ATLANTEAN_RIFLE)
                || !(event.getSource().getEntity() instanceof LivingEntity shooter)) {
            return;
        }
        double bonus = SkillsHelper.attr(shooter, GunneryRegistry.GUN_DAMAGE)
                + SkillsHelper.attr(shooter, GunneryRegistry.GUN_BULLET_SPEED);
        if (bonus > 0.0) {
            event.setAmount(event.getAmount() * (float) (1.0 + bonus));
        }
    }

    public static void onRifleHit(LivingDamageEvent.Post event) {
        if (!event.getSource().is(ATLANTEAN_RIFLE)
                || !(event.getSource().getEntity() instanceof LivingEntity shooter)) {
            return;
        }
        double strength = SkillsHelper.attr(shooter, GunneryRegistry.GUN_KNOCKBACK);
        LivingEntity target = event.getEntity();
        if (strength > 0.0 && target != shooter) {
            target.knockback(strength, shooter.getX() - target.getX(), shooter.getZ() - target.getZ());
        }
    }

    public static void onComposeShot(ComposeShotEvent event) {
        LivingEntity shooter = event.getEntity();
        ShotProfile profile = event.getShotProfile();
        multiply(profile, ShotComponents.DAMAGE, SkillsHelper.attr(shooter, GunneryRegistry.GUN_DAMAGE));
        multiply(profile, ShotComponents.FIRE_RATE, SkillsHelper.attr(shooter, GunneryRegistry.GUN_FIRE_RATE));
        multiply(profile, ShotComponents.RELOAD_SPEED_MULTIPLIER, SkillsHelper.attr(shooter, GunneryRegistry.GUN_RELOAD_SPEED));
        multiply(profile, ShotComponents.BULLET_SPEED, SkillsHelper.attr(shooter, GunneryRegistry.GUN_BULLET_SPEED));
        multiply(profile, ShotComponents.SPREAD, -SkillsHelper.attr(shooter, GunneryRegistry.GUN_ACCURACY));
        multiply(profile, ShotComponents.AMMO_CONSUME_CHANCE, -SkillsHelper.attr(shooter, GunneryRegistry.GUN_AMMO_SAVE));
        add(profile, ShotComponents.KNOCKBACK, SkillsHelper.attr(shooter, GunneryRegistry.GUN_KNOCKBACK));
        add(profile, ShotComponents.SEEKING, SkillsHelper.attr(shooter, GunneryRegistry.GUN_SEEKING));
        add(profile, ShotComponents.ACCELERATING, Math.floor(SkillsHelper.attr(shooter, GunneryRegistry.GUN_RAMPING)));
        add(profile, ShotComponents.PIERCING, Math.floor(SkillsHelper.attr(shooter, GunneryRegistry.GUN_PIERCING)));
        add(profile, ShotComponents.RICOCHET, Math.floor(SkillsHelper.attr(shooter, GunneryRegistry.GUN_RICOCHET)));
        multiply(profile, ShotComponents.CAMERA_RECOIL_MULTIPLIER, -SkillsHelper.attr(shooter, GunneryRegistry.GUN_RECOIL_CONTROL));
        multiply(profile, ShotComponents.IN_AIR_PENALTY, -SkillsHelper.attr(shooter, GunneryRegistry.GUN_AIR_ACCURACY));
        double deadEye = SkillsHelper.attr(shooter, GunneryRegistry.GUN_DEAD_EYE);
        if (deadEye > 0.0
                && shooter.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.TRICORNE_HAT)
                && profile.magazineContents().count() == profile.gun().magazineCapacity()) {
            multiply(profile, ShotComponents.DAMAGE, deadEye / (1.0 + TricorneItem.DAMAGE_BUFF_PERCENT));
        }
    }

    public static void onGunShootPre(GunShootEvent.Pre event) {
        LivingEntity shooter = event.getEntity();
        double chance = SkillsHelper.attr(shooter, GunneryRegistry.GUN_EXTRA_BULLET);
        if (chance <= 0.0 || shooter.getRandom().nextDouble() >= chance) {
            return;
        }
        ShotProfile profile = event.getShotProfile();
        double count = profile.value(ShotComponents.PROJECTILE_COUNT);
        add(profile, ShotComponents.PROJECTILE_COUNT, 1.0);
        multiply(profile, ShotComponents.DAMAGE, Math.max(1.0, count + 1.0) / Math.max(1.0, count) - 1.0);
    }

    private static void multiply(ShotProfile profile, ComponentType<Value> type, double amount) {
        if (amount != 0.0) {
            profile.modifyValue(
                    type,
                    new ValueModifier(amount, ValueModifier.Operation.MULTIPLY_TOTAL, ValueModifier.Type.BENEFICIAL));
        }
    }

    private static void add(ShotProfile profile, ComponentType<Value> type, double amount) {
        if (amount != 0.0) {
            profile.modifyValue(type, new ValueModifier(amount, ValueModifier.Operation.ADD, ValueModifier.Type.BENEFICIAL));
        }
    }
}
