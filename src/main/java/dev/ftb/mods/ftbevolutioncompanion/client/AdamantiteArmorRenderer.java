package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import dev.ftb.mods.ftbevolutioncompanion.metals.AdamantiteArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class AdamantiteArmorRenderer extends GeoArmorRenderer<AdamantiteArmorItem, HumanoidRenderState> {
    public AdamantiteArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath("ftb", "adamantite_armor")));
    }
}
