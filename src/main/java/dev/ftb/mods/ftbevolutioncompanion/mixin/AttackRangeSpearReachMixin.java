package dev.ftb.mods.ftbevolutioncompanion.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsHelper;
import dev.ftb.mods.ftbevolutioncompanion.skills.SkillsRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.AttackRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AttackRange.class)
public abstract class AttackRangeSpearReachMixin {
    @ModifyReturnValue(method = "effectiveMaxRange", at = @At("RETURN"))
    private float ftbevo$spearReach(float original, Entity entity) {
        if (entity instanceof Player player && SkillsHelper.isSpear(player.getMainHandItem())) {
            return original + (float) SkillsHelper.attr(player, SkillsRegistry.SPEAR_REACH);
        }
        return original;
    }
}
