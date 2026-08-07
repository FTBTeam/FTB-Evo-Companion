package dev.ftb.mods.ftbevolutioncompanion.skills;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.BleedingEffect;
import dev.ftb.mods.ftbevolutioncompanion.skills.effect.StunnedEffect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public final class SkillsRegistry {
    public static final String NAMESPACE = "ftb";

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, NAMESPACE);
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, NAMESPACE);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, FTBEvolutionCompanion.MOD_ID);

    private static final List<DeferredHolder<Attribute, Attribute>> PLAYER_ATTRIBUTES = new ArrayList<>();

    public static final DeferredHolder<Attribute, Attribute> ARROW_SAVE = attr("arrow_save", 1.0);
    public static final DeferredHolder<Attribute, Attribute> MULTISHOT_CHANCE = attr("multishot_chance", 1.0);
    public static final DeferredHolder<Attribute, Attribute> RAMPING_SHOTS = attr("ramping_shots", 10.0);
    public static final DeferredHolder<Attribute, Attribute> FIRST_STRIKE = attr("first_strike", 2.0);
    public static final DeferredHolder<Attribute, Attribute> BOW_DURABILITY = attr("bow_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> CROSSBOW_DURABILITY = attr("crossbow_durability", 20.0);

    public static final DeferredHolder<Attribute, Attribute> UNARMED_RESISTANCE = attr("unarmed_resistance", 1.0);
    public static final DeferredHolder<Attribute, Attribute> UNARMED_KILL_HEAL = attr("unarmed_kill_heal", 1.0);
    public static final DeferredHolder<Attribute, Attribute> COMBO_PUNCH = attr("combo_punch", 2.0);
    public static final DeferredHolder<Attribute, Attribute> FASTER_STRIKES = attr("faster_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> UNARMED_RAMP = attr("unarmed_ramp", 1.0);

    public static final DeferredHolder<Attribute, Attribute> AXE_FRENZY = attr("axe_frenzy", 2.0);
    public static final DeferredHolder<Attribute, Attribute> AXE_DESPERATION = attr("axe_desperation", 10.0);
    public static final DeferredHolder<Attribute, Attribute> CHEAT_DEATH = attr("cheat_death", 1.0);
    public static final DeferredHolder<Attribute, Attribute> DEATH_BLOW = attr("death_blow", 3.0);
    public static final DeferredHolder<Attribute, Attribute> DUAL_WIELD = attr("dual_wield", 2.0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTNING_STRIKES = attr("lightning_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> BLOODLUST = attr("bloodlust", 100.0);
    public static final DeferredHolder<Attribute, Attribute> AXE_DURABILITY = attr("axe_durability", 20.0);

    public static final DeferredHolder<Attribute, Attribute> SHIELD_STUN = attr("shield_stun", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_RECOVERY = attr("shield_recovery", 1.0);
    public static final DeferredHolder<Attribute, Attribute> GROUND_SLAM = attr("ground_slam", 3.0);
    public static final DeferredHolder<Attribute, Attribute> LIGHTS_SHIELD = attr("lights_shield", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_MASTERY = attr("shield_mastery", 20.0);
    public static final DeferredHolder<Attribute, Attribute> SHIELD_DURABILITY = attr("shield_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> DAY_DAMAGE = attr("day_damage", 2.0);

    public static final DeferredHolder<Attribute, Attribute> BACKSTAB = attr("backstab", 3.0);
    public static final DeferredHolder<Attribute, Attribute> SHADOW_STEP = attr("shadow_step", 1.0);
    public static final DeferredHolder<Attribute, Attribute> ECHO_STRIKES = attr("echo_strikes", 1.0);
    public static final DeferredHolder<Attribute, Attribute> SWORD_DURABILITY = attr("sword_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> STEALTH = attr("stealth", 1.0);
    public static final DeferredHolder<Attribute, Attribute> NINJA = attr("ninja", 60.0);
    public static final DeferredHolder<Attribute, Attribute> SHAKEDOWN = attr("shakedown", 1.0);
    public static final DeferredHolder<Attribute, Attribute> NIGHT_DAMAGE = attr("night_damage", 2.0);
    public static final DeferredHolder<Attribute, Attribute> BLADEMASTER = attr("blademaster", 1.0);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_DURABILITY = attr("armor_durability", 20.0);
    public static final DeferredHolder<Attribute, Attribute> MINING_FORTUNE = attr("mining_fortune", 20.0);
    public static final DeferredHolder<Attribute, Attribute> PICKAXE_DURABILITY = attr("pickaxe_durability", 20.0);

    public static final DeferredHolder<MobEffect, MobEffect> STUNNED = EFFECTS.register("stunned", StunnedEffect::new);
    public static final DeferredHolder<MobEffect, MobEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillToggles>> TOGGLES =
            ATTACHMENTS.register("skill_toggles",
                    () -> AttachmentType.builder(() -> SkillToggles.DEFAULT)
                            .serialize(SkillToggles.CODEC)
                            .copyOnDeath()
                            .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<SkillCooldowns>> COOLDOWNS =
            ATTACHMENTS.register("skill_cooldowns",
                    () -> AttachmentType.builder(() -> SkillCooldowns.EMPTY)
                            .serialize(SkillCooldowns.CODEC)
                            .copyOnDeath()
                            .build());
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CombatState>> COMBAT_STATE =
            ATTACHMENTS.register("skills_combat_state",
                    () -> AttachmentType.builder(CombatState::new).build());

    private SkillsRegistry() {
    }

    private static DeferredHolder<Attribute, Attribute> attr(String name, double max) {
        DeferredHolder<Attribute, Attribute> holder = ATTRIBUTES.register(name,
                () -> new RangedAttribute("attribute.name.ftb." + name, 0.0, 0.0, max).setSyncable(true));
        PLAYER_ATTRIBUTES.add(holder);
        return holder;
    }

    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        for (DeferredHolder<Attribute, Attribute> holder : PLAYER_ATTRIBUTES) {
            event.add(EntityType.PLAYER, holder);
        }
    }
}
