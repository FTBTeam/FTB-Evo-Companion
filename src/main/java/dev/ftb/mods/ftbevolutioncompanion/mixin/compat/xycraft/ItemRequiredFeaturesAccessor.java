package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.xycraft;

import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemRequiredFeaturesAccessor {
    @Accessor("requiredFeatures")
    @Mutable
    void ftbevo$setRequiredFeatures(FeatureFlagSet features);
}
