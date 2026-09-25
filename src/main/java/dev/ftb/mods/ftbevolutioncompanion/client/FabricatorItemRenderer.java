package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorItem;

public final class FabricatorItemRenderer extends GeoItemRenderer<FabricatorItem> {
    public FabricatorItemRenderer() {
        super(new DefaultedBlockGeoModel<>(FTBEvolutionCompanion.id("ftb_fabricator")));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> pass) {
        super.adjustRenderPose(pass);
        // The block model spans Y=0..16; item transforms rotate around its center.
        pass.poseStack().translate(0F, -0.5F, 0F);
    }
}
