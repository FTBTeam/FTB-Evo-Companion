package dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.client;

import dev.ftb.mods.ftbevolutioncompanion.magic.MagicRegistry;
import dev.ftb.mods.ftbevolutioncompanion.magic.hedgecraft.CovenMagic;

import dev.sterner.witchery.content.item.WitcheryPotionIngredient;
import dev.sterner.witchery.content.item.WitcheryPotionItem;

import net.minecraft.client.Minecraft;

import java.util.List;

public final class CovenMagicClient {
    private CovenMagicClient() {
    }

    public static WitcheryPotionIngredient.EffectModifier brewModifier(List<WitcheryPotionIngredient> ingredients,
                                                                       WitcheryPotionIngredient.EffectModifier modifier) {
        if (ingredients == null || ingredients.isEmpty()
                || WitcheryPotionItem.Companion.resolvePotionType(ingredients) == WitcheryPotionIngredient.Type.LINGERING) {
            return modifier;
        }
        return CovenMagic.brewModifier(Minecraft.getInstance().player, modifier);
    }

    public static int riteAltarPower(int amount) {
        if (amount <= 0) {
            return amount;
        }
        double efficiency = MagicRegistry.value(Minecraft.getInstance().player, MagicRegistry.WITCHERY_ALTAR_EFFICIENCY);
        return efficiency <= 0.0 ? amount : (int) Math.ceil(amount * (1.0 - efficiency));
    }
}
