package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.buildingwand.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.in2bubble.architecturalbuildingwand.client.PlannerManager", remap = false)
public abstract class PlannerWorldIdSanitizeMixin {
    @ModifyReturnValue(method = "getUniqueWorldId", at = @At("RETURN"))
    private static String ftbevo$stripIllegalFileNameChars(String worldId) {
        return worldId.replaceAll("[<>:\"/\\\\|?*\\p{Cntrl}]", "_");
    }
}
