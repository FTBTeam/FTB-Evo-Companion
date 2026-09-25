package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import dev.ftb.mods.ftbevolutioncompanion.metals.AeterniumArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class AeterniumArmorRenderer extends GeoArmorRenderer<AeterniumArmorItem, HumanoidRenderState> {
    public AeterniumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath("ftb", "aeternium_armor")));
    }
}
