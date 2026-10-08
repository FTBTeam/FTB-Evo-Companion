package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.xycraft;

import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.soaryn.xycraft.machines.content.items.modular206.FlareRodItem;

@Mixin(FlareRodItem.class)
public abstract class XyCraftFlareRodFeatureMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void ftbevo$dropExperimentalFlag(Item.Properties properties, CallbackInfo ci) {
        ((ItemRequiredFeaturesAccessor) this).ftbevo$setRequiredFeatures(FeatureFlags.VANILLA_SET);
    }
}
