package dev.ftb.mods.ftbevolutioncompanion;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import java.util.Set;

public final class XpFluidUnifier {
    private static final Identifier CANONICAL_FLUID_ID =
            Identifier.fromNamespaceAndPath("justdirethings", "xp_fluid_source");
    private static final Identifier CANONICAL_BUCKET_ID =
            Identifier.fromNamespaceAndPath("justdirethings", "xp_fluid_bucket");

    private static final Set<Identifier> REPLACED_FLUIDS = Set.of(
            Identifier.fromNamespaceAndPath("mobflowutilities", "liquid_xp"),
            Identifier.fromNamespaceAndPath("sophisticatedcore", "xp_still"),
            Identifier.fromNamespaceAndPath("reliquary", "xp_still"),
            Identifier.fromNamespaceAndPath("energizedpower", "liquid_xp"),
            Identifier.fromNamespaceAndPath("enderio", "fluid_xp_juice_still"));

    private static final Set<Identifier> REPLACED_BUCKETS = Set.of(
            Identifier.fromNamespaceAndPath("mobflowutilities", "liquid_xp_bucket"));

    private static Fluid canonicalFluid;
    private static Item canonicalBucket;
    private static DeferredHolder<Fluid, Fluid> canonicalHolder;

    private XpFluidUnifier() {
    }

    public static Fluid canonicalFluid() {
        if (canonicalFluid == null) {
            Fluid found = BuiltInRegistries.FLUID.getValue(CANONICAL_FLUID_ID);
            canonicalFluid = found == Fluids.EMPTY ? null : found;
        }
        return canonicalFluid;
    }

    public static Item canonicalBucket() {
        if (canonicalBucket == null) {
            Item found = BuiltInRegistries.ITEM.getValue(CANONICAL_BUCKET_ID);
            canonicalBucket = found == Items.AIR ? null : found;
        }
        return canonicalBucket;
    }

    public static Object substitute(Object original) {
        if (original instanceof Fluid fluid) {
            if (!REPLACED_FLUIDS.contains(BuiltInRegistries.FLUID.getKey(fluid))) {
                return original;
            }
            Fluid canonical = canonicalFluid();
            return canonical == null ? original : canonical;
        }
        if (original instanceof Item item) {
            if (!REPLACED_BUCKETS.contains(BuiltInRegistries.ITEM.getKey(item))) {
                return original;
            }
            Item canonical = canonicalBucket();
            return canonical == null ? original : canonical;
        }
        return original;
    }

    public static DeferredHolder<Fluid, Fluid> canonicalHolder() {
        if (canonicalHolder == null) {
            canonicalHolder = DeferredHolder.create(Registries.FLUID, CANONICAL_FLUID_ID);
        }
        return canonicalHolder;
    }

    public static DeferredHolder<?, ?> substituteHolder(DeferredHolder<?, ?> original) {
        return REPLACED_FLUIDS.contains(original.getId()) ? canonicalHolder() : original;
    }

    public static FluidResource substituteResource(FluidResource original) {
        Fluid fluid = original.getFluid();
        Object substituted = substitute(fluid);
        return substituted == fluid ? original : FluidResource.of((Fluid) substituted);
    }
}
