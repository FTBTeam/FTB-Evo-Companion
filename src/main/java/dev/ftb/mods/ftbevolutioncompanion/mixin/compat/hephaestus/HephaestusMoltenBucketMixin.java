package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.hephaestus;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.LavaFluid;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.titammods.setup.ModFluids$MoltenFluid$MoltenBase", remap = false)
public abstract class HephaestusMoltenBucketMixin extends LavaFluid {
    @Override
    public Item getBucket() {
        Item bucket = BuiltInRegistries.ITEM.getValue(
                BuiltInRegistries.FLUID.getKey(this.getSource()).withSuffix("_bucket"));
        return bucket == Items.AIR ? super.getBucket() : bucket;
    }
}
