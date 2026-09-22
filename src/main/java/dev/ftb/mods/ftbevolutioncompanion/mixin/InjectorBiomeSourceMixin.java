package dev.ftb.mods.ftbevolutioncompanion.mixin;

import dev.ftb.mods.ftbevolutioncompanion.spawn.PyramidBiome;
import dev.ftb.mods.ftbevolutioncompanion.spawn.PyramidBiomeSource;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "dev.worldgen.lithostitched.impl.worldgen.biomeinjector.internal.InjectorBiomeSource", remap = false)
public abstract class InjectorBiomeSourceMixin implements PyramidBiomeSource {
    @Unique
    private boolean ftbevo$pyramidSource;

    @Override
    public boolean ftbevo$isPyramidSource() {
        return this.ftbevo$pyramidSource;
    }

    @Override
    public void ftbevo$setPyramidSource(boolean pyramidSource) {
        this.ftbevo$pyramidSource = pyramidSource;
    }

    @Inject(method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"), cancellable = true)
    private void ftbevolutioncompanion$pyramidBiome(int x, int y, int z, Climate.Sampler sampler,
                                                    CallbackInfoReturnable<Holder<Biome>> cir) {
        if (!this.ftbevo$pyramidSource) return;
        Holder<Biome> override = PyramidBiome.overrideInBox(x, y, z);
        if (override != null) cir.setReturnValue(override);
    }
}
