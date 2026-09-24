package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.fabricator.FabricatorItem;

public final class FabricatorItemRenderer extends GeoItemRenderer<FabricatorItem> {
    public FabricatorItemRenderer() {
        super(new DefaultedBlockGeoModel<>(FTBEvolutionCompanion.id("ftb_fabricator")));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
