package dev.ftb.mods.ftbevolutioncompanion.gunnery;

import dev.ftb.mods.ftbevolutioncompanion.mixin.compat.apothic.BulletModifierAffixAccessor;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;

import dev.shadowsoffire.apotheosis.affix.Affix;
import dev.shadowsoffire.apotheosis.affix.AffixHelper;
import dev.shadowsoffire.apotheosis.affix.AffixInstance;
import dev.shadowsoffire.apotheosis.util.DamageSourceExtension;

import ianm1647.apothic_compats.affix.irons_artifice.BulletModifierAffix;
import ianm1647.apothic_compats.affix.irons_artifice.InfiniteAmmoAffix;
import ianm1647.apothic_compats.affix.irons_artifice.MagicalBulletAffix;

import io.redspace.irons_artifice.data.RecentShots;
import io.redspace.irons_artifice.item.CowboyHatItem;
import io.redspace.irons_artifice.item.TricorneItem;
import io.redspace.irons_artifice.modifier.modifiers.MechanicalAccelerator;
import io.redspace.irons_artifice.registry.ItemRegistry;
import io.redspace.irons_artifice.registry.SoundRegistry;

import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.entity.HomingProjectileEntity;
import liedge.ltxindustries.entity.LTXIEntityUtil;
import liedge.ltxindustries.entity.LTXIProjectileEntity;
import liedge.ltxindustries.entity.damage.EquipmentDamageSource;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.Upgrades;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class LtxGunnery {
    private static final Identifier APOTHIC_GUN_DAMAGE = Identifier.fromNamespaceAndPath("apothic_compats", "gun_damage");
    private static final Identifier APOTHIC_BULLET_PIERCE =
            Identifier.fromNamespaceAndPath("apothic_compats", "bullet_pierce");
    private static final String FULL_MAG_TAG = "ftbevo_full_mag";
    private static final double SEEKING_HITBOX = 0.75;
    private static final double SEEKING_RANGE = 64.0;
    private static final ThreadLocal<Boolean> FULL_MAG_SHOT = ThreadLocal.withInitial(() -> false);
    private static final Map<UUID, Deque<Long>> RECENT_SHOTS = new HashMap<>();

    private LtxGunnery() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(EventPriority.LOW, LtxGunnery::onKill);
        NeoForge.EVENT_BUS.addListener(LtxGunnery::onEntityJoin);
        NeoForge.EVENT_BUS.addListener(LtxGunnery::onInvulnerabilityCheck);
        NeoForge.EVENT_BUS.addListener(LtxGunnery::onLogout);
    }

    public static void fire(Player player, ItemStack stack, WeaponItem weapon, Runnable shot) {
        if (player.level().isClientSide()) {
            shot.run();
            return;
        }
        FULL_MAG_SHOT.set(player.getItemBySlot(EquipmentSlot.HEAD).is(ItemRegistry.TRICORNE_HAT)
                && weapon.getAmmoLoaded(stack) >= weapon.getAmmoCapacity(stack));
        try {
            shot.run();
        } finally {
            FULL_MAG_SHOT.set(false);
        }
        long now = player.level().getGameTime();
        Deque<Long> shots = RECENT_SHOTS.computeIfAbsent(player.getUUID(), id -> new ArrayDeque<>());
        prune(shots, now);
        shots.addLast(now);
    }

    public static float modifyDamage(LivingEntity shooter, DamageSource source, float amount) {
        double damage = apothic(shooter, APOTHIC_GUN_DAMAGE, amount);
        double skill = SkillsHelper.attr(shooter, GunneryRegistry.GUN_DAMAGE)
                + SkillsHelper.attr(shooter, GunneryRegistry.GUN_BULLET_SPEED);
        damage *= 1.0 + skill;
        double ramping = Math.floor(SkillsHelper.attr(shooter, GunneryRegistry.GUN_RAMPING));
        if (ramping > 0.0) {
            damage *= 1.0 + recentShots(shooter) * MechanicalAccelerator.DAMAGE_PER_SHOT * ramping;
        }
        if (isFullMagShot(source)) {
            damage *= 1.0 + TricorneItem.DAMAGE_BUFF_PERCENT + SkillsHelper.attr(shooter, GunneryRegistry.GUN_DEAD_EYE);
        }
        return (float) damage;
    }

    public static void onHit(LivingEntity shooter, Entity target, DamageSource source) {
        if (!(source instanceof EquipmentDamageSource equipment) || !(target instanceof LivingEntity living)) {
            return;
        }
        AffixHelper.streamAffixes(equipment.getWeaponItem()).forEach(inst -> {
            if (inst.getAffix() instanceof BulletModifierAffix affix) {
                BulletModifierAffixAccessor accessor = (BulletModifierAffixAccessor) affix;
                LivingEntity affected =
                        accessor.ftbevo$target() == BulletModifierAffix.Target.BULLET_SELF ? shooter : living;
                accessor.ftbevo$applyEffect(affected, inst.getRarity(), inst.level());
            }
        });
    }

    public static int bonusPierce(LivingEntity shooter) {
        return (int) Math.floor(SkillsHelper.attr(shooter, GunneryRegistry.GUN_PIERCING))
                + (int) Math.floor(apothic(shooter, APOTHIC_BULLET_PIERCE, 0.0));
    }

    public static boolean hasInfiniteAmmo(ItemStack stack) {
        return hasAffix(stack, InfiniteAmmoAffix.class);
    }

    public static double seekingHitbox(LivingEntity source) {
        if (source instanceof Player player && player.getMainHandItem().getItem() instanceof WeaponItem) {
            return Math.min(1.0, SkillsHelper.attr(player, GunneryRegistry.GUN_SEEKING)) * SEEKING_HITBOX;
        }
        return 0.0;
    }

    public static void seekTarget(Player player, ItemStack stack, WeaponItem weapon, HomingProjectileEntity rocket) {
        double seeking = Math.min(1.0, SkillsHelper.attr(player, GunneryRegistry.GUN_SEEKING));
        if (seeking <= 0.0 || rocket.getTargetEntity() != null) {
            return;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        double minDot = Math.cos(Math.toRadians(5.0 + 20.0 * seeking));
        Upgrades upgrades = weapon.getUpgrades(stack);
        player.level()
                .getEntities(
                        player,
                        player.getBoundingBox()
                                .expandTowards(look.scale(SEEKING_RANGE))
                                .inflate(SEEKING_RANGE * 0.5),
                        e -> e instanceof LivingEntity
                                && LTXIEntityUtil.isEntityAlive(e)
                                && LTXIEntityUtil.isValidContextTarget(e, player, upgrades))
                .stream()
                .filter(e -> {
                    Vec3 toTarget = e.getBoundingBox().getCenter().subtract(eye);
                    return toTarget.length() <= SEEKING_RANGE && look.dot(toTarget.normalize()) >= minDot;
                })
                .filter(player::hasLineOfSight)
                .max(Comparator.comparingDouble(e -> look.dot(
                        e.getBoundingBox().getCenter().subtract(eye).normalize())))
                .ifPresent(rocket::setTargetEntity);
    }

    private static void onKill(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        if (!source.is(LTXITags.DamageTypes.WEAPONS) || !(source.getEntity() instanceof Player player)) {
            return;
        }
        ItemStack hat = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack gun = player.getMainHandItem();
        if (!hat.is(ItemRegistry.COWBOY_HAT)
                || !(gun.getItem() instanceof WeaponItem weapon)
                || player.getCooldowns().isOnCooldown(hat)) {
            return;
        }
        int missing = weapon.getAmmoCapacity(gun) - weapon.getAmmoLoaded(gun);
        if (missing <= 0) {
            return;
        }
        weapon.setAmmoLoadedMax(gun);
        player.level()
                .playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundRegistry.INSTANT_RELOAD.get(),
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F);
        double quickdraw = SkillsHelper.attr(player, GunneryRegistry.GUN_QUICKDRAW);
        int cooldown = CowboyHatItem.COOLDOWN_TICKS;
        player.getCooldowns()
                .addCooldown(
                        hat, quickdraw > 0.0 ? Math.max(1, (int) Math.round(cooldown * (1.0 - quickdraw))) : cooldown);
        player.sendOverlayMessage(Component.translatable("item.irons_artifice.cowboy_hat.ability.gain_ammo", missing)
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }

    private static void onEntityJoin(EntityJoinLevelEvent event) {
        if (FULL_MAG_SHOT.get() && event.getEntity() instanceof LTXIProjectileEntity projectile) {
            projectile.addTag(FULL_MAG_TAG);
        }
    }

    private static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getSource() instanceof EquipmentDamageSource source
                && source.is(LTXITags.DamageTypes.WEAPONS)
                && hasAffix(source.getWeaponItem(), MagicalBulletAffix.class)) {
            DamageSourceExtension extension = (DamageSourceExtension) event.getSource();
            extension.addTag(Tags.DamageTypes.IS_MAGIC);
            extension.addTag(DamageTypeTags.BYPASSES_ARMOR);
        }
    }

    private static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        RECENT_SHOTS.remove(event.getEntity().getUUID());
    }

    private static boolean isFullMagShot(DamageSource source) {
        Entity direct = source.getDirectEntity();
        return FULL_MAG_SHOT.get() || (direct != null && direct.entityTags().contains(FULL_MAG_TAG));
    }

    private static int recentShots(LivingEntity shooter) {
        Deque<Long> shots = RECENT_SHOTS.get(shooter.getUUID());
        if (shots == null) {
            return 0;
        }
        prune(shots, shooter.level().getGameTime());
        return shots.size();
    }

    private static void prune(Deque<Long> shots, long now) {
        while (!shots.isEmpty() && now - shots.peekFirst() >= RecentShots.WINDOW_TICKS) {
            shots.pollFirst();
        }
    }

    private static boolean hasAffix(ItemStack stack, Class<? extends Affix> type) {
        return !stack.isEmpty()
                && AffixHelper.streamAffixes(stack).map(AffixInstance::getAffix).anyMatch(type::isInstance);
    }

    private static double apothic(LivingEntity entity, Identifier id, double base) {
        AttributeInstance instance =
                BuiltInRegistries.ATTRIBUTE.get(id).map(entity::getAttribute).orElse(null);
        if (instance == null) {
            return base;
        }
        double add = 0.0;
        double baseMultiplier = 0.0;
        double total = 1.0;
        for (AttributeModifier modifier : instance.getModifiers()) {
            switch (modifier.operation()) {
                case ADD_VALUE -> add += modifier.amount();
                case ADD_MULTIPLIED_BASE -> baseMultiplier += modifier.amount();
                case ADD_MULTIPLIED_TOTAL -> total *= 1.0 + modifier.amount();
            }
        }
        return (base + add + base * baseMultiplier) * total;
    }
}
