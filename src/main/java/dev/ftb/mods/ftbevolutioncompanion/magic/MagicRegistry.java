package dev.ftb.mods.ftbevolutioncompanion.magic;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class MagicRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SkillsRegistry.NAMESPACE);
    public static final DeferredRegister<Identifier> STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, SkillsRegistry.NAMESPACE);

    private static final List<DeferredHolder<Attribute, Attribute>> PLAYER_ATTRIBUTES = new ArrayList<>();
    private static final List<DeferredHolder<Identifier, Identifier>> CUSTOM_STATS = new ArrayList<>();

    public static final DeferredHolder<Attribute, Attribute> ARS_COST_REDUCTION = attr("ars_cost_reduction", 0.9);
    public static final DeferredHolder<Attribute, Attribute> ARS_SPELL_POWER = attr("ars_spell_power", 2.0);
    public static final DeferredHolder<Attribute, Attribute> ARS_XP_GAIN = attr("ars_xp_gain", 2.0);
    public static final DeferredHolder<Attribute, Attribute> THAUM_FOCUS_POWER = attr("thaum_focus_power", 2.0);
    public static final DeferredHolder<Attribute, Attribute> THAUM_WARP_WARD = attr("thaum_warp_ward", 0.9);
    public static final DeferredHolder<Attribute, Attribute> THAUM_INFUSION_STABILITY = attr("thaum_infusion_stability", 10.0);
    public static final DeferredHolder<Attribute, Attribute> ENCHANTING_ARCANA = attr("enchanting_arcana", 50.0);
    public static final DeferredHolder<Attribute, Attribute> ENCHANTING_DISCOUNT = attr("enchanting_discount", 0.9);

    public static final DeferredHolder<Attribute, Attribute> ROOTS_POTENCY = attr("roots_potency", 3.0);
    public static final DeferredHolder<Attribute, Attribute> ROOTS_COST_REDUCTION = attr("roots_cost_reduction", 0.9);
    public static final DeferredHolder<Attribute, Attribute> ROOTS_STAFF_SAVE = attr("roots_staff_save", 0.9);
    public static final DeferredHolder<Attribute, Attribute> ROOTS_RITUAL_SPEED = attr("roots_ritual_speed", 0.9);
    public static final DeferredHolder<Attribute, Attribute> WITCH_POWER = attr("witch_power", 6.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_ALTAR_EFFICIENCY = attr("witchery_altar_efficiency", 0.9);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_INFUSION_GAIN = attr("witchery_infusion_gain", 2.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_INFUSION_EFFICIENCY = attr("witchery_infusion_efficiency", 0.9);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_BREW_BOTTLING = attr("witchery_brew_bottling", 1.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_BREW_POTENCY = attr("witchery_brew_potency", 2.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_SYMBOL_MASTERY = attr("witchery_symbol_mastery", 4.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_POPPET_DURABILITY = attr("witchery_poppet_durability", 0.9);

    public static final DeferredHolder<Attribute, Attribute> VITAE_RITUAL_EFFICIENCY = attr("vitae_ritual_efficiency", 0.9);
    public static final DeferredHolder<Attribute, Attribute> VITAE_SENTIENT_GROWTH = attr("vitae_sentient_growth", 2.0);
    public static final DeferredHolder<Attribute, Attribute> EVILCRAFT_BLOOD_EFFICIENCY = attr("evilcraft_blood_efficiency", 0.9);
    public static final DeferredHolder<Attribute, Attribute> EVILCRAFT_BLOOD_HARVEST = attr("evilcraft_blood_harvest", 2.0);
    public static final DeferredHolder<Attribute, Attribute> EVILCRAFT_BROOM_SPEED = attr("evilcraft_broom_speed", 2.0);
    public static final DeferredHolder<Attribute, Attribute> EVILCRAFT_SPIRIT_BINDING = attr("evilcraft_spirit_binding", 20.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_ABILITY_COOLDOWN = attr("witchery_ability_cooldown", 0.9);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_NECROMANCY = attr("witchery_necromancy", 2.0);
    public static final DeferredHolder<Attribute, Attribute> WITCHERY_LICH_SOULS = attr("witchery_lich_souls", 3.0);

    public static final DeferredHolder<Attribute, Attribute> OCCULT_RITUAL_HASTE = attr("occult_ritual_haste", 3.0);
    public static final DeferredHolder<Attribute, Attribute> OCCULT_SPIRIT_DILIGENCE = attr("occult_spirit_diligence", 2.0);
    public static final DeferredHolder<Attribute, Attribute> OCCULT_SPIRIT_BOUNTY = attr("occult_spirit_bounty", 1.0);
    public static final DeferredHolder<Attribute, Attribute> OCCULT_FAMILIAR_BOND = attr("occult_familiar_bond", 2.0);
    public static final DeferredHolder<Attribute, Attribute> ANIMA_SOUL_THRIFT = attr("anima_soul_thrift", 0.9);
    public static final DeferredHolder<Attribute, Attribute> ANIMA_SPELL_HASTE = attr("anima_spell_haste", 0.9);
    public static final DeferredHolder<Attribute, Attribute> ANIMA_REAPING = attr("anima_reaping", 2.0);
    public static final DeferredHolder<Attribute, Attribute> ANIMA_WORKER_ENDURANCE = attr("anima_worker_endurance", 2.0);

    public static final DeferredHolder<Identifier, Identifier> SPELLS_CAST = stat("spells_cast");
    public static final DeferredHolder<Identifier, Identifier> RITES_PERFORMED = stat("rites_performed");
    public static final DeferredHolder<Identifier, Identifier> BREWS_BOTTLED = stat("brews_bottled");
    public static final DeferredHolder<Identifier, Identifier> RESEARCH_COMPLETED = stat("research_completed");
    public static final DeferredHolder<Identifier, Identifier> AFFLICTION_LEVELS = stat("affliction_levels");

    private MagicRegistry() {
    }

    private static DeferredHolder<Attribute, Attribute> attr(String name, double max) {
        DeferredHolder<Attribute, Attribute> holder = ATTRIBUTES.register(name,
                () -> new RangedAttribute("attribute.name.ftb." + name, 0.0, 0.0, max).setSyncable(true));
        PLAYER_ATTRIBUTES.add(holder);
        return holder;
    }

    private static DeferredHolder<Identifier, Identifier> stat(String name) {
        DeferredHolder<Identifier, Identifier> holder = STATS.register(name,
                () -> Identifier.fromNamespaceAndPath(SkillsRegistry.NAMESPACE, name));
        CUSTOM_STATS.add(holder);
        return holder;
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (DeferredHolder<Attribute, Attribute> holder : PLAYER_ATTRIBUTES) {
            event.add(EntityType.PLAYER, holder);
        }
    }

    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for (DeferredHolder<Identifier, Identifier> holder : CUSTOM_STATS) {
                Stats.CUSTOM.get(holder.get(), StatFormatter.DEFAULT);
            }
        });
    }

    public static double value(LivingEntity entity, Holder<Attribute> attribute) {
        if (entity == null) {
            return 0.0;
        }
        AttributeInstance instance = entity.getAttribute(attribute);
        return instance == null ? 0.0 : instance.getValue();
    }

    public static void award(ServerPlayer player, DeferredHolder<Identifier, Identifier> stat) {
        player.awardStat(Stats.CUSTOM.get(stat.get()));
    }
}
