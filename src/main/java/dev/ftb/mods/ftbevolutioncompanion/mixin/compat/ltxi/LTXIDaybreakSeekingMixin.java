package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.ltxi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ftb.mods.ftbevolutioncompanion.gunnery.LtxGunnery;
import liedge.ltxindustries.entity.HomingProjectileEntity;
import liedge.ltxindustries.item.weapon.DaybreakItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DaybreakItem.class, remap = false)
public abstract class LTXIDaybreakSeekingMixin {
    @WrapOperation(
            method = "weaponFired",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean ftbevo$seeking(
            Level level,
            Entity entity,
            Operation<Boolean> original,
            @Local(argsOnly = true) ItemStack stack,
            @Local(argsOnly = true) Player player) {
        if (entity instanceof HomingProjectileEntity rocket) {
            LtxGunnery.seekTarget(player, stack, (DaybreakItem) (Object) this, rocket);
        }
        return original.call(level, entity);
    }
}
