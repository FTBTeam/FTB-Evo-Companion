package dev.ftb.mods.ftbevolutioncompanion.content;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class CompanionTags {
    public static final TagKey<Block> ODD_BERRY_BUSH_SPREADABLE =
            TagKey.create(Registries.BLOCK, FTBEvolutionCompanion.id("odd_berry_bush_spreadable"));

    private CompanionTags() {
    }
}
