package dev.ftb.mods.ftbevolutioncompanion.client;

import com.geckolib.model.DefaultedBlockGeoModel;
import com.geckolib.renderer.GeoBlockRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.EvolutionPyramidBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.LaunchTask;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.FTBQuestsClientEventHandler;
import dev.ftb.mods.ftbquests.quest.task.EnergyTask;
import dev.ftb.mods.ftbquests.quest.task.FluidTask;
import dev.ftb.mods.ftbquests.quest.task.ItemTask;
import dev.ftb.mods.ftbquests.quest.task.Task;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EvolutionPyramidRenderer extends GeoBlockRenderer<EvolutionPyramidBlockEntity, EvolutionPyramidRenderer.State> {
    private static final String LANG = "ftbevolutioncompanion.evolution_pyramid.";
    private static final double HOLOGRAM_Y = 7.25D;
    private static final int TEXT_BACKGROUND = 0x40000000;
    private static final float TEXT_SCALE = 0.025F;

    private final ItemModelResolver itemModelResolver;
    private final Font font;

    public EvolutionPyramidRenderer(BlockEntityRendererProvider.Context context) {
        super(context, new DefaultedBlockGeoModel<>(FTBEvolutionCompanion.id("evolution_pyramid")));
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
        withRenderLayer(AutoGlowingGeoLayer::new);
    }

    public static class State extends BlockEntityRenderState {
        boolean hologram;
        final ItemStackRenderState item = new ItemStackRenderState();
        boolean hasItem;
        @Nullable
        TextureAtlasSprite baseSprite;
        @Nullable
        TextureAtlasSprite fillSprite;
        int baseTint = 0xFFFFFFFF;
        boolean fillBase;
        float progress;
        float spin;
        Component title = Component.empty();
        Component detail = Component.empty();
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(EvolutionPyramidBlockEntity machine, State state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumbling) {
        super.extractRenderState(machine, state, partialTick, cameraPos, crumbling);

        state.hologram = false;
        state.hasItem = false;
        state.baseSprite = null;
        state.fillSprite = null;
        state.item.clear();

        if (machine.isLaunching() || machine.getActiveTaskId() == 0L || machine.getLevel() == null || !ClientQuestFile.exists()) return;
        Task task = ClientQuestFile.getInstance().getTask(machine.getActiveTaskId());
        if (task == null) return;

        state.hologram = true;
        long time = machine.getLevel().getGameTime();
        state.spin = (time + partialTick) * 2F % 360F;

        if (task instanceof LaunchTask) {
            state.title = task.getQuest().getTitle();
            state.detail = Component.translatable(LANG + "ready_to_launch").withStyle(ChatFormatting.GREEN);
            itemModelResolver.updateForTopItem(state.item, machine.getBlockState().getBlock().asItem().getDefaultInstance(), ItemDisplayContext.FIXED, machine.getLevel(), null, 0);
            state.hasItem = !state.item.isEmpty();
            return;
        }

        long max = Math.max(1L, task.getMaxProgress());
        long progress = Math.min(machine.getDisplayProgress(), max);
        state.progress = (float) progress / max;
        state.title = task.getTitle();
        ChatFormatting color = progress >= max ? ChatFormatting.GREEN : progress > 0 ? ChatFormatting.YELLOW : ChatFormatting.GOLD;
        state.detail = Component.literal(task.formatProgress(ClientQuestFile.getInstance().selfTeamData, progress) + " / " + task.formatMaxProgress()).withStyle(color);

        if (task instanceof ItemTask itemTask) {
            List<ItemStack> stacks = itemTask.getValidDisplayItems();
            ItemStack shown = stacks.isEmpty() ? itemTask.getItemStack() : stacks.get((int) (time / 20L % stacks.size()));
            itemModelResolver.updateForTopItem(state.item, shown, ItemDisplayContext.FIXED, machine.getLevel(), null, 0);
            state.hasItem = !state.item.isEmpty();
        } else if (task instanceof FluidTask fluidTask && FTBQuestsClientEventHandler.tankSprite != null) {
            FluidState fluid = fluidTask.getFluid().defaultFluidState();
            var model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluid);
            state.baseSprite = model.stillMaterial().sprite();
            state.baseTint = 0xFF000000 | (model.tintSource() == null ? 0xFFFFFF : model.tintSource().color(fluid.createLegacyBlock()));
            state.fillBase = true;
            state.fillSprite = FTBQuestsClientEventHandler.tankSprite;
        } else if (task instanceof EnergyTask energyTask) {
            state.baseSprite = energyTask.getClientData().getEmptyTexture();
            state.baseTint = 0xFFFFFFFF;
            state.fillBase = false;
            state.fillSprite = energyTask.getClientData().getFullTexture();
        }
    }

    @Override
    protected void tryRotateByBlockstate(RenderPassInfo<State> info, PoseStack poseStack) {
        Direction facing = info.getOrDefaultGeckolibData(DIRECTION_FACING, Direction.NORTH);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F - facing.toYRot()));
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        super.submit(state, poseStack, collector, camera);
        if (state.hologram) {
            submitHologram(state, poseStack, collector, camera);
        }
    }

    private void submitHologram(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5D, HOLOGRAM_Y, 0.5D);

        if (state.hasItem) {
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(state.spin));
            poseStack.scale(1.25F, 1.25F, 1.25F);
            state.item.submit(poseStack, collector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        } else if (state.baseSprite != null && state.fillSprite != null) {
            submitGauge(state, poseStack, collector, camera);
        }

        submitLabel(state.title, 0.95D, poseStack, collector, camera);
        submitLabel(state.detail, -0.85D, poseStack, collector, camera);
        poseStack.popPose();
    }

    private void submitGauge(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        TextureAtlasSprite base = state.baseSprite;
        TextureAtlasSprite fill = state.fillSprite;
        float baseAmount = state.fillBase ? state.progress : 1F;
        float fillAmount = state.fillBase ? 1F : state.progress;
        int baseTint = state.baseTint;

        poseStack.pushPose();
        poseStack.mulPose(camera.orientation);
        poseStack.scale(1.1F, 1.1F, 1.1F);
        collector.submitCustomGeometry(poseStack, RenderTypes.text(base.atlasLocation()), (pose, consumer) -> {
            if (baseAmount > 0F) {
                gaugeQuad(pose, consumer, base, baseAmount, 0F, baseTint);
            }
            if (fillAmount > 0F) {
                gaugeQuad(pose, consumer, fill, fillAmount, 0.002F, 0xFFFFFFFF);
            }
        });
        poseStack.popPose();
    }

    private static void gaugeQuad(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite, float amount, float z, int color) {
        Matrix4f matrix = pose.pose();
        float bottom = -0.5F;
        float top = bottom + amount;
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV(1F - amount);
        float v1 = sprite.getV1();
        int light = LightCoordsUtil.FULL_BRIGHT;
        consumer.addVertex(matrix, -0.5F, bottom, z).setColor(color).setUv(u0, v1).setLight(light);
        consumer.addVertex(matrix, 0.5F, bottom, z).setColor(color).setUv(u1, v1).setLight(light);
        consumer.addVertex(matrix, 0.5F, top, z).setColor(color).setUv(u1, v0).setLight(light);
        consumer.addVertex(matrix, -0.5F, top, z).setColor(color).setUv(u0, v0).setLight(light);
        consumer.addVertex(matrix, -0.5F, top, z).setColor(color).setUv(u0, v0).setLight(light);
        consumer.addVertex(matrix, 0.5F, top, z).setColor(color).setUv(u1, v0).setLight(light);
        consumer.addVertex(matrix, 0.5F, bottom, z).setColor(color).setUv(u1, v1).setLight(light);
        consumer.addVertex(matrix, -0.5F, bottom, z).setColor(color).setUv(u0, v1).setLight(light);
    }

    private void submitLabel(Component text, double y, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (text.getString().isEmpty()) return;
        poseStack.pushPose();
        poseStack.translate(0D, y, 0D);
        poseStack.mulPose(camera.orientation);
        poseStack.scale(TEXT_SCALE, -TEXT_SCALE, TEXT_SCALE);
        float x = -font.width(text) / 2F;
        collector.submitText(poseStack, x, 0F, text.getVisualOrderText(), false, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, 0xFFFFFFFF, TEXT_BACKGROUND, 0);
        poseStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(EvolutionPyramidBlockEntity machine) {
        return new AABB(machine.getBlockPos()).inflate(3D, 0D, 3D).expandTowards(0D, 14D, 0D);
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
