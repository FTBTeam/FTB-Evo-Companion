package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;

import dev.ftb.mods.ftbevolutioncompanion.content.BeastTrophyBlockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;

public class BeastTrophyRenderer extends GeoBlockRenderer<BeastTrophyBlockEntity, BeastTrophyRenderer.State> {
    public BeastTrophyRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DefaultedBlockGeoModel<>(Identifier.fromNamespaceAndPath("ftb", "beast_trophy")));
        withRenderLayer(AutoGlowingGeoLayer::new);
    }

    public static class State extends BlockEntityRenderState {
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public AABB getRenderBoundingBox(BeastTrophyBlockEntity trophy) {
        return new AABB(trophy.getBlockPos()).expandTowards(0D, 1D, 0D);
    }
}
