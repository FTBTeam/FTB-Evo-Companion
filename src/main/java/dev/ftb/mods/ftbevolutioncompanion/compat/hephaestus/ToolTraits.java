package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ToolTraits {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToolTraits.class);

    public enum Trait {
        MAGICAL("magical"),
        MAGICALLY_JAGGED("magically_jagged"),
        DEMONIC("demonic"),
        NAUTICAL("nautical"),
        DRAGONFIRE("dragonfire"),
        DRAGONFROST("dragonfrost"),
        DRAGONSTORM("dragonstorm");

        private final String id;

        Trait(String id) {
            this.id = id;
        }

        public String key() {
            return "trait.ftbevolutioncompanion." + id;
        }
    }

    private static final Map<String, Trait> BY_MATERIAL = Map.of(
            "thaumium", Trait.MAGICAL,
            "void_metal", Trait.MAGICALLY_JAGGED,
            "hellforged", Trait.DEMONIC,
            "atlantic_gold", Trait.NAUTICAL,
            "aquarine_steel", Trait.NAUTICAL,
            "dragonsteel_fire", Trait.DRAGONFIRE,
            "dragonsteel_ice", Trait.DRAGONFROST,
            "dragonsteel_lightning", Trait.DRAGONSTORM);

    private static final Identifier BLOOD_SIPHON = Identifier.fromNamespaceAndPath("neovitae", "blood_siphon");
    private static final Identifier BLOOD_MENDING = Identifier.fromNamespaceAndPath("neovitae", "blood_mending");
    private static final Identifier STACK_WARP = Identifier.fromNamespaceAndPath("thaumaturge", "warp");
    private static final Identifier RUNIC_SHIELDING = Identifier.fromNamespaceAndPath("thaumaturge", "runic_shielding");
    private static final Identifier FROZEN = Identifier.fromNamespaceAndPath("iceandfire", "frozen");
    private static final Identifier SIPHON_MODIFIER = Identifier.fromNamespaceAndPath("ftb", "demonic_blood_siphon");
    private static final String ORB_CLASS = "com.leclowndu93150.thaumaturge.content.wands.EntityAspectOrb";
    private static final String ASPECTS_CLASS = "com.leclowndu93150.thaumaturge.api.aspect.TCAspects";

    private static final float ORB_CHANCE = 0.01F;
    private static final float RUNIC_CHANCE = 0.15F;
    private static final float VOID_MAGIC_DAMAGE = 3.0F;
    private static final int DRAGON_SECONDS = 15;
    private static final int STORM_TARGETS = 10;
    private static final double STORM_RANGE = 10.0;
    private static final int NAUTICAL_LEVEL = 3;

    private static final ThreadLocal<Boolean> DEALING = ThreadLocal.withInitial(() -> false);
    private static final Set<ItemStack> SUBMERGED =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private static Constructor<?> orbConstructor;
    private static List<?> primals;
    private static boolean orbFailed;

    private ToolTraits() {}

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ToolTraits::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onDamagePost);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onBlockBreak);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onBlockDrops);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onBreakSpeed);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ToolTraits::onEnchantmentLevel);
    }

    public static Set<Trait> traits(ItemStack stack) {
        Set<Trait> traits = EnumSet.noneOf(Trait.class);
        for (Identifier material : HephaestusTools.materials(stack)) {
            if ("hephaestus_tools".equals(material.getNamespace())) {
                Trait trait = BY_MATERIAL.get(material.getPath());
                if (trait != null) {
                    traits.add(trait);
                }
            }
        }
        return traits;
    }

    private static boolean magical(Set<Trait> traits) {
        return traits.contains(Trait.MAGICAL) || traits.contains(Trait.MAGICALLY_JAGGED);
    }

    public static void appendTooltip(ItemStack stack, Consumer<Component> tooltip) {
        for (Trait trait : traits(stack)) {
            tooltip.accept(Component.translatable(trait.key()).withStyle(ChatFormatting.DARK_AQUA));
        }
    }

    public static void onRecalculate(ItemStack stack) {
        Set<Trait> traits = traits(stack);
        DataComponentType<Integer> warp = component(STACK_WARP);
        if (warp != null) {
            if (traits.contains(Trait.MAGICALLY_JAGGED)) {
                stack.set(warp, 1);
            } else {
                stack.remove(warp);
            }
        }
        if (!traits.contains(Trait.DEMONIC)) {
            return;
        }
        BuiltInRegistries.ATTRIBUTE.get(BLOOD_SIPHON).ifPresent(siphon -> {
            ItemAttributeModifiers modifiers =
                    stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
            stack.set(
                    DataComponents.ATTRIBUTE_MODIFIERS,
                    modifiers.withModifierAdded(
                            siphon,
                            new AttributeModifier(SIPHON_MODIFIER, 2.0, AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND));
        });
        DataComponentType<Boolean> mending = component(BLOOD_MENDING);
        if (mending != null) {
            stack.set(mending, true);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> DataComponentType<T> component(Identifier id) {
        return (DataComponentType<T>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(id);
    }

    private static Player meleeAttacker(net.minecraft.world.damagesource.DamageSource source) {
        if (source.getEntity() instanceof Player player && source.getDirectEntity() == player) {
            return player;
        }
        return null;
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        Player player = meleeAttacker(event.getSource());
        if (player == null || DEALING.get()) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        Set<Trait> traits = traits(stack);
        if (traits.contains(Trait.MAGICALLY_JAGGED) || traits.contains(Trait.DEMONIC)) {
            event.setAmount(HephaestusTools.jagged(player, event.getEntity(), stack, event.getAmount()));
        }
    }

    private static void onDamagePost(LivingDamageEvent.Post event) {
        Player player = meleeAttacker(event.getSource());
        if (player == null || DEALING.get() || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity target = event.getEntity();
        ItemStack stack = player.getMainHandItem();
        Set<Trait> traits = traits(stack);
        if (traits.isEmpty()) {
            return;
        }
        if (traits.contains(Trait.DRAGONFIRE)) {
            target.igniteForSeconds(DRAGON_SECONDS);
            target.knockback(1.0, player.getX() - target.getX(), player.getZ() - target.getZ());
        }
        if (traits.contains(Trait.DRAGONFROST)) {
            int ticks = DRAGON_SECONDS * 20;
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, ticks, 2));
            target.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, ticks, 2));
            BuiltInRegistries.MOB_EFFECT
                    .get(FROZEN)
                    .ifPresent(frozen -> target.addEffect(new MobEffectInstance(frozen, ticks, 0)));
        }
        if (traits.contains(Trait.DRAGONSTORM) && target instanceof Mob) {
            chainLightning(level, player, target, event.getHealthDamage());
        }
        if (traits.contains(Trait.MAGICALLY_JAGGED)) {
            deal(level, target, level.damageSources().indirectMagic(player, player), VOID_MAGIC_DAMAGE);
            restoreRunicShield(player);
        }
    }

    private static void deal(
            ServerLevel level,
            LivingEntity target,
            net.minecraft.world.damagesource.DamageSource source,
            float amount) {
        if (!target.isAlive() || amount <= 0.0F) {
            return;
        }
        DEALING.set(true);
        try {
            target.invulnerableTime = 0;
            target.hurtServer(level, source, amount);
        } finally {
            DEALING.set(false);
        }
    }

    private static void chainLightning(ServerLevel level, Player player, LivingEntity first, float damage) {
        Set<LivingEntity> struck = new java.util.HashSet<>();
        struck.add(first);
        Deque<LivingEntity> queue = new ArrayDeque<>();
        queue.add(first);
        float amount = damage;
        while (!queue.isEmpty() && struck.size() <= STORM_TARGETS) {
            LivingEntity from = queue.poll();
            amount *= 0.5F;
            if (amount < 0.5F) {
                break;
            }
            AABB area = from.getBoundingBox().inflate(STORM_RANGE);
            for (Mob next : level.getEntitiesOfClass(Mob.class, area, mob -> mob.isAlive() && !struck.contains(mob))) {
                if (struck.size() > STORM_TARGETS) {
                    break;
                }
                if (!player.hasLineOfSight(next)) {
                    continue;
                }
                struck.add(next);
                queue.add(next);
                arc(
                        level,
                        from.position().add(0, from.getBbHeight() / 2, 0),
                        next.position().add(0, next.getBbHeight() / 2, 0));
                deal(level, next, level.damageSources().playerAttack(player), amount);
            }
        }
        if (struck.size() > 1) {
            level.playSound(
                    null,
                    first.getX(),
                    first.getY(),
                    first.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundSource.PLAYERS,
                    0.6F,
                    1.4F);
        }
    }

    private static void arc(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 step = to.subtract(from);
        int points = Math.max(4, (int) (step.length() * 3));
        for (int i = 0; i <= points; i++) {
            Vec3 at = from.add(step.scale((double) i / points));
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, at.x, at.y, at.z, 1, 0.05, 0.05, 0.05, 0.0);
        }
    }

    private static void restoreRunicShield(Player player) {
        if (player.getRandom().nextFloat() >= RUNIC_CHANCE) {
            return;
        }
        AttributeInstance attribute = player.getAttribute(Attributes.MAX_ABSORPTION);
        if (attribute == null) {
            return;
        }
        AttributeModifier shield = attribute.getModifier(RUNIC_SHIELDING);
        if (shield != null && shield.amount() > player.getAbsorptionAmount()) {
            player.setAbsorptionAmount((float) shield.amount());
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof Player player)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (magical(traits(player.getMainHandItem()))) {
            LivingEntity dead = event.getEntity();
            maybeOrb(level, dead.getX(), dead.getY() + dead.getBbHeight() / 2, dead.getZ(), player.getRandom());
        }
    }

    private static void onBlockBreak(BreakBlockEvent event) {
        Player player = event.getPlayer();
        if (player != null && traits(player.getMainHandItem()).contains(Trait.MAGICALLY_JAGGED)) {
            restoreRunicShield(player);
        }
    }

    private static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }
        Set<Trait> traits = traits(event.getTool());
        if (!magical(traits)) {
            return;
        }
        ServerLevel level = event.getLevel();
        BlockState state = event.getState();
        RandomSource random = player.getRandom();
        if (state.is(Tags.Blocks.ORES)) {
            maybeOrb(
                    level,
                    event.getPos().getX() + 0.5,
                    event.getPos().getY() + 0.5,
                    event.getPos().getZ() + 0.5,
                    random);
        }
        Identifier block = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if ("thaumaturge".equals(block.getNamespace()) && block.getPath().startsWith("crystal_")) {
            for (ItemEntity drop : event.getDrops()) {
                ItemStack item = drop.getItem();
                item.grow(item.getCount() * random.nextInt(3));
            }
        }
    }

    private static synchronized boolean orbsReady() {
        if (orbFailed) {
            return false;
        }
        if (orbConstructor != null) {
            return true;
        }
        try {
            Class<?> orb = Class.forName(ORB_CLASS);
            orbConstructor = orb.getConstructor(
                    Level.class, double.class, double.class, double.class, ResourceKey.class, int.class);
            primals = (List<?>) Class.forName(ASPECTS_CLASS).getField("PRIMALS").get(null);
            return true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            orbFailed = true;
            LOGGER.error("Thaumaturge aspect orbs are unavailable", e);
            return false;
        }
    }

    private static void maybeOrb(ServerLevel level, double x, double y, double z, RandomSource random) {
        if (random.nextFloat() >= ORB_CHANCE || !orbsReady() || primals.isEmpty()) {
            return;
        }
        try {
            Object aspect = primals.get(random.nextInt(primals.size()));
            Object orb = orbConstructor.newInstance(level, x, y, z, aspect, 1 + random.nextInt(3));
            level.addFreshEntity((net.minecraft.world.entity.Entity) orb);
        } catch (ReflectiveOperationException | RuntimeException e) {
            LOGGER.warn("Could not spawn an aspect orb", e);
        }
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        if (!player.isEyeInFluid(FluidTags.WATER)
                || !traits(player.getMainHandItem()).contains(Trait.NAUTICAL)) {
            return;
        }
        float speed = event.getNewSpeed();
        double submerged = player.getAttributeValue(Attributes.SUBMERGED_MINING_SPEED);
        if (submerged > 0.0 && submerged < 1.0) {
            speed /= (float) submerged;
        }
        if (!player.onGround()) {
            speed *= 5.0F;
        }
        event.setNewSpeed(speed);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (player.isEyeInFluid(FluidTags.WATER) && traits(held).contains(Trait.NAUTICAL)) {
            SUBMERGED.add(held);
        } else {
            SUBMERGED.remove(held);
        }
        if (player instanceof ServerPlayer && player.tickCount % 20 == 0) {
            repairVoidTools(player);
        }
    }

    private static void repairVoidTools(Player player) {
        List<ItemStack> stacks = new ArrayList<>(player.getInventory().getNonEquipmentItems());
        stacks.add(player.getOffhandItem());
        for (ItemStack stack : stacks) {
            if (stack.isDamaged() && traits(stack).contains(Trait.MAGICALLY_JAGGED)) {
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        }
    }

    private static void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        if (!(event.getStack() instanceof ItemStack stack) || !SUBMERGED.contains(stack)) {
            return;
        }
        boostTo(event, Enchantments.FORTUNE);
        boostTo(event, Enchantments.LOOTING);
    }

    private static void boostTo(
            GetEnchantmentLevelEvent event, ResourceKey<net.minecraft.world.item.enchantment.Enchantment> key) {
        if (!event.isTargetting(key)) {
            return;
        }
        event.getHolder(key).ifPresent(holder -> {
            if (event.getEnchantments().getLevel(holder) < NAUTICAL_LEVEL) {
                event.getEnchantments().set(holder, NAUTICAL_LEVEL);
            }
        });
    }
}
