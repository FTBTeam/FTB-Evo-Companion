package dev.ftb.mods.ftbevolutioncompanion.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(JigsawPlacement.class)
public abstract class JigsawPlacementRotationMixin {
    private static final Logger ftbevolutioncompanion$LOGGER = LoggerFactory.getLogger("PyramidRotation");
    private static final Set<String> ftbevolutioncompanion$SEEN = ConcurrentHashMap.newKeySet();

    @Inject(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure$MaxDistance;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;",
            at = @At("HEAD")
    )
    private static void ftbevolutioncompanion$logStartPool(
            Structure.GenerationContext context,
            Holder<StructureTemplatePool> startPool,
            Optional<Identifier> startJigsawName,
            int maxDepth,
            BlockPos pos,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            JigsawStructure.MaxDistance maxDistanceFromCenter,
            PoolAliasLookup aliasLookup,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir
    ) {
        String id = startPool.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse("<direct holder, no key>");
        if (ftbevolutioncompanion$SEEN.add("head:" + id)) {
            ftbevolutioncompanion$LOGGER.info("addPieces entered with start pool {} at {}", id, pos);
        }
    }

    @Redirect(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;Lnet/minecraft/world/level/levelgen/structure/structures/JigsawStructure$MaxDistance;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Rotation;getRandom(Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/Rotation;")
    )
    private static Rotation ftbevolutioncompanion$forceSpawnPyramidRotation(
            RandomSource random,
            Structure.GenerationContext context,
            Holder<StructureTemplatePool> startPool,
            Optional<Identifier> startJigsawName,
            int maxDepth,
            BlockPos pos,
            boolean useExpansionHack,
            Optional<Heightmap.Types> projectStartToHeightmap,
            JigsawStructure.MaxDistance maxDistanceFromCenter,
            PoolAliasLookup aliasLookup,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings
    ) {
        Rotation rolled = Rotation.getRandom(random);
        String id = startPool.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse("<direct holder, no key>");
        if (ftbevolutioncompanion$SEEN.add(id)) {
            ftbevolutioncompanion$LOGGER.info("jigsaw rotation hook saw start pool {}", id);
        }
        if (!id.contains("spawn_pyramid")) return rolled;
        ftbevolutioncompanion$LOGGER.info("forcing rotation NONE for the spawn pyramid jigsaw (rolled {})", rolled);
        return Rotation.NONE;
    }
}
