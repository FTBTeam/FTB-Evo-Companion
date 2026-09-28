package dev.ftb.mods.ftbevolutioncompanion.gunnery;

import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

public final class GunneryRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SkillsRegistry.NAMESPACE);

    private static final List<DeferredHolder<Attribute, Attribute>> PLAYER_ATTRIBUTES = new ArrayList<>();

    public static final DeferredHolder<Attribute, Attribute> GUN_DAMAGE = attr("gun_damage", 2.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_FIRE_RATE = attr("gun_fire_rate", 1.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_RELOAD_SPEED = attr("gun_reload_speed", 2.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_BULLET_SPEED = attr("gun_bullet_speed", 2.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_ACCURACY = attr("gun_accuracy", 0.9);
    public static final DeferredHolder<Attribute, Attribute> GUN_AMMO_SAVE = attr("gun_ammo_save", 0.9);
    public static final DeferredHolder<Attribute, Attribute> GUN_EXTRA_BULLET = attr("gun_extra_bullet", 1.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_KNOCKBACK = attr("gun_knockback", 5.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_SEEKING = attr("gun_seeking", 1.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_RAMPING = attr("gun_ramping", 5.0);
    public static final DeferredHolder<Attribute, Attribute> GUN_QUICKDRAW = attr("gun_quickdraw", 0.9);
    public static final DeferredHolder<Attribute, Attribute> GUN_DEAD_EYE = attr("gun_dead_eye", 3.0);

    private GunneryRegistry() {
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
