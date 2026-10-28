package dev.ftb.mods.ftbevolutioncompanion.compat.hephaestus;

import com.mojang.serialization.MapCodec;
import com.titammods.hephaestus_tools.api.event.ToolRecalculatedEvent;
import com.titammods.hephaestus_tools.effect.EffectRegistries;
import com.titammods.hephaestus_tools.effect.ToolEffect;
import com.titammods.hephaestus_tools.materials.trait.ToolTraits;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class HephaestusTraits {
    private static final DeferredRegister<MapCodec<? extends ToolEffect>> EFFECT_TYPES =
            DeferredRegister.create(EffectRegistries.EFFECT_TYPE_KEY, FTBEvolutionCompanion.MOD_ID);

    private static final Identifier DEMONIC = Identifier.fromNamespaceAndPath(FTBEvolutionCompanion.MOD_ID, "demonic");
    private static final Identifier NAUTICAL =
            Identifier.fromNamespaceAndPath(FTBEvolutionCompanion.MOD_ID, "nautical");
    private static final Identifier BLOOD_SIPHON = Identifier.fromNamespaceAndPath("neovitae", "blood_siphon");
    private static final Identifier BLOOD_MENDING = Identifier.fromNamespaceAndPath("neovitae", "blood_mending");
    private static final Identifier SIPHON_MODIFIER = Identifier.fromNamespaceAndPath("ftb", "demonic_blood_siphon");
    private static final int NAUTICAL_LEVEL = 3;

    private static final Set<ItemStack> SUBMERGED =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private HephaestusTraits() {}

    public static void register(IEventBus eventBus) {
        EFFECT_TYPES.register("submerged_mining", () -> SubmergedMiningEffect.CODEC);
        EFFECT_TYPES.register("ignite", () -> IgniteEffect.CODEC);
        EFFECT_TYPES.register("freeze", () -> FreezeEffect.CODEC);
        EFFECT_TYPES.register("chain_lightning", () -> ChainLightningEffect.CODEC);
        EFFECT_TYPES.register(eventBus);
        NeoForge.EVENT_BUS.addListener(HephaestusTraits::onRecalculated);
        NeoForge.EVENT_BUS.addListener(HephaestusTraits::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(HephaestusTraits::onEnchantmentLevel);
    }

    private static boolean has(ItemStack stack, Identifier trait) {
        return !stack.isEmpty() && ToolTraits.level(stack, trait) > 0;
    }

    private static void onRecalculated(ToolRecalculatedEvent event) {
        ItemStack stack = event.getStack();
        if (!has(stack, DEMONIC)) {
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
        @SuppressWarnings("unchecked")
        DataComponentType<Boolean> mending =
                (DataComponentType<Boolean>) BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(BLOOD_MENDING);
        if (mending != null) {
            stack.set(mending, true);
        }
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        ItemStack held = player.getMainHandItem();
        if (player.isEyeInFluid(FluidTags.WATER) && has(held, NAUTICAL)) {
            SUBMERGED.add(held);
        } else {
            SUBMERGED.remove(held);
        }
    }

    private static void onEnchantmentLevel(GetEnchantmentLevelEvent event) {
        if (!(event.getStack() instanceof ItemStack stack) || !SUBMERGED.contains(stack)) {
            return;
        }
        boostTo(event, Enchantments.FORTUNE);
        boostTo(event, Enchantments.LOOTING);
    }

    private static void boostTo(GetEnchantmentLevelEvent event, ResourceKey<Enchantment> key) {
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
