package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.SkylineItem;

import net.minecraft.world.item.ItemDisplayContext;

public class SkylineItemRenderer extends GeoItemRenderer<SkylineItem> {
    private static final float HALF_HEIGHT = 3F;

    public SkylineItemRenderer() {
        super(new DefaultedBlockGeoModel<>(FTBEvolutionCompanion.id("ftb_skyline")));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    private static float scaleFor(ItemDisplayContext context) {
        return switch (context) {
            case GUI -> 0.22F;
            case FIXED -> 0.2F;
            case GROUND -> 0.12F;
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> 0.1F;
            default -> 0.15F;
        };
    }

    @Override
    public void adjustRenderPose(RenderPassInfo<GeoRenderState> pass) {
        super.adjustRenderPose(pass);
        PoseStack poseStack = pass.poseStack();
        float scale = scaleFor(pass.renderState().getOrDefaultGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE, ItemDisplayContext.GUI));
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0F, -HALF_HEIGHT, 0F);
    }
}
