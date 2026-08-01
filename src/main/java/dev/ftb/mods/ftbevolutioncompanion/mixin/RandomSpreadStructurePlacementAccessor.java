package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RandomSpreadStructurePlacement.class)
public interface RandomSpreadStructurePlacementAccessor {
    @Mutable
    @Accessor("spacing")
    void ftbevo$setSpacing(int spacing);

    @Mutable
    @Accessor("separation")
    void ftbevo$setSeparation(int separation);
}
