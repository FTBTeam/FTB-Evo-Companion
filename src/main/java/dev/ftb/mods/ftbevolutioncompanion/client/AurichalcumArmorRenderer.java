package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import dev.ftb.mods.ftbevolutioncompanion.metals.AurichalcumArmorItem;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;

public final class AurichalcumArmorRenderer extends GeoArmorRenderer<AurichalcumArmorItem, HumanoidRenderState> {
    public AurichalcumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Identifier.fromNamespaceAndPath("ftb", "aurichalcum_armor")));
    }
}
