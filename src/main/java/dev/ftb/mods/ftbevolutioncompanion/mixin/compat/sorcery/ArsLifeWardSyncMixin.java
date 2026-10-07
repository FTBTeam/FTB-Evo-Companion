package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.sorcery;

import at.minecraftschurli.mods.arsmagicalegacy.attachment.LifeWardAttachment;
import at.minecraftschurli.mods.arsmagicalegacy.init.AMAttachments;
import at.minecraftschurli.mods.arsmagicalegacy.util.AMEquipmentUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.function.Supplier;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = AMEquipmentUtil.class, remap = false)
public abstract class ArsLifeWardSyncMixin {
    @WrapOperation(
            method = "tickLifeWard",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/entity/LivingEntity;setData(Ljava/util/function/Supplier;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object ftbevo$skipUnchangedLifeWard(
            LivingEntity entity, Supplier<?> type, Object value, Operation<Object> original) {
        if (value instanceof LifeWardAttachment ward
                && ward.isEmpty()
                && (!entity.hasData(AMAttachments.LIFE_WARD)
                        || entity.getData(AMAttachments.LIFE_WARD).isEmpty())) {
            return null;
        }
        return original.call(entity, type, value);
    }
}
