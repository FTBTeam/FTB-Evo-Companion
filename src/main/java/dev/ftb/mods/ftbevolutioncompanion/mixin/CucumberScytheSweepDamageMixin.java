package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.blakebr0.cucumber.item.tool.BaseScytheItem", remap = false)
public abstract class CucumberScytheSweepDamageMixin {
    @ModifyReturnValue(method = "getAttackDamage()F", at = @At("RETURN"))
    private float ftbevo$currentAttackDamage(float original) {
        ItemAttributeModifiers modifiers = ((Item) (Object) this).components().get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) {
            return original;
        }
        for (ItemAttributeModifiers.Entry entry : modifiers.modifiers()) {
            if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)
                    && entry.modifier().id().equals(Item.BASE_ATTACK_DAMAGE_ID)) {
                return (float) entry.modifier().amount();
            }
        }
        return original;
    }
}
