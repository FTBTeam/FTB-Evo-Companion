package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.apothic;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.shadowsoffire.apotheosis.compat.gateways.InvaderWaveEntity;
import dev.shadowsoffire.apotheosis.compat.gateways.TrueRandomInvaderWaveEntity;
import dev.shadowsoffire.apotheosis.mobs.registries.InvaderRegistry;
import dev.shadowsoffire.apotheosis.mobs.types.Invader;
import dev.shadowsoffire.apotheosis.tiers.GenContext;
import java.util.Map;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = InvaderWaveEntity.class, remap = false)
public abstract class InvaderWaveDimensionFallbackMixin {
    @Unique
    private static final Map<Identifier, ResourceKey<Level>> ftbevo$DIMENSION_ALIASES = Map.of(
            Identifier.fromNamespaceAndPath("jamd", "mining"), Level.OVERWORLD,
            Identifier.fromNamespaceAndPath("jamd", "nether"), Level.NETHER,
            Identifier.fromNamespaceAndPath("jamd", "end"), Level.END);

    @WrapOperation(
            method = "resolveInvader",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Ldev/shadowsoffire/apotheosis/mobs/registries/InvaderRegistry;"
                                    + "getRandomItem(Ldev/shadowsoffire/apotheosis/tiers/GenContext;)"
                                    + "Ldev/shadowsoffire/apotheosis/mobs/types/Invader;"))
    private Invader ftbevo$fallBackOutsideVanillaDimensions(
            InvaderRegistry registry, GenContext ctx, Operation<Invader> original) {
        Invader invader = original.call(registry, ctx);
        if (invader != null) {
            return invader;
        }
        ResourceKey<Level> alias =
                ftbevo$DIMENSION_ALIASES.getOrDefault(ctx.dimension().identifier(), Level.OVERWORLD);
        if (!alias.equals(ctx.dimension())) {
            invader = original.call(
                    registry, new GenContext(ctx.rand(), ctx.tier(), ctx.luck(), alias, ctx.biome(), ctx.stages()));
            if (invader != null) {
                return invader;
            }
        }
        return TrueRandomInvaderWaveEntity.getTrulyRandomItem(registry, ctx);
    }
}
