package dev.ftb.mods.ftbevolutioncompanion.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardBlock;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeBoardBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeClientData;
import dev.ftb.mods.ftbevolutioncompanion.challenge.ChallengeSnapshot;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.quest.Chapter;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.jspecify.annotations.Nullable;

public class ChallengeBoardRenderer implements BlockEntityRenderer<ChallengeBoardBlockEntity, ChallengeBoardRenderState> {
    private static final String LANG = "block.ftbevolutioncompanion.challenge_board.";
    private static final int TEXT_COLOR = 0xFFD8D8D8;
    private static final int BAR_BACKGROUND = 0xFF1A1A1A;
    private static final int BAR_COMPLETE = 0xFF55FF55;
    private static final int BAR_PARTIAL = 0xFFFFAA00;
    private static final float BAR_LEFT = 0.1F;
    private static final float BAR_RIGHT = 0.9F;
    private static final float BAR_TOP = 0.46F;
    private static final float BAR_BOTTOM = 0.55F;

    private final Font font;

    public ChallengeBoardRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.font();
    }

    @Override
    public ChallengeBoardRenderState createRenderState() {
        return new ChallengeBoardRenderState();
    }

    @Override
    public void extractRenderState(ChallengeBoardBlockEntity board, ChallengeBoardRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(board, state, partialTick, cameraPos, crumblingOverlay);

        ChallengeSnapshot snapshot = ChallengeClientData.snapshot();
        state.rotationAngle = board.getBlockState().getValue(ChallengeBoardBlock.FACING).toYRot() + 180F;
        state.width = board.getWidth();
        state.height = board.getHeight();
        state.title = chapterTitle(snapshot);

        Component rank = Component.translatable(LANG + "rank", board.getRank()).withStyle(ChatFormatting.GOLD);
        ChallengeSnapshot.Entry entry = snapshot.entry(board.getRank()).orElse(null);
        state.hasEntry = entry != null;
        if (entry != null) {
            state.headline = rank.copy().append(" ").append(entry.teamName());
            state.percentText = Component.translatable(LANG + "percent", entry.percent())
                    .withStyle(entry.percent() >= 100 ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
            state.countText = Component.translatable(LANG + "quests", entry.completed(), entry.total()).withStyle(ChatFormatting.GRAY);
            state.progress = Math.clamp(entry.percent() / 100F, 0F, 1F);
            state.barColor = entry.percent() >= 100 ? BAR_COMPLETE : BAR_PARTIAL;
        } else {
            state.headline = rank;
            state.emptyText = Component.translatable(LANG + "waiting").withStyle(ChatFormatting.GRAY);
        }
    }

    private static Component chapterTitle(ChallengeSnapshot snapshot) {
        if (snapshot.hasChapter() && ClientQuestFile.exists()) {
            Chapter chapter = ClientQuestFile.getInstance().getChapter(snapshot.chapterId());
            if (chapter != null) return chapter.getTitle();
        }
        return Component.translatable(LANG + "title");
    }

    @Override
    public void submit(ChallengeBoardRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotationAngle));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        int leftCount = (state.width - 1) / 2;
        poseStack.translate(-leftCount, -(state.height - 1), -0.02F);
        poseStack.scale(state.height, state.height, 1F);
        float aspect = (float) state.width / state.height;

        drawString(collector, poseStack, state.title, aspect, 0.04D, 0.11F);
        drawString(collector, poseStack, state.headline, aspect, 0.20D, 0.16F);

        if (state.hasEntry) {
            float barLeft = BAR_LEFT * aspect;
            float barRight = BAR_RIGHT * aspect;
            float fillRight = barLeft + (barRight - barLeft) * state.progress;
            int barColor = state.barColor;
            collector.submitCustomGeometry(poseStack, RenderTypes.textBackground(), (pose, consumer) -> {
                quad(pose, consumer, barLeft, BAR_TOP, barRight, BAR_BOTTOM, 0F, BAR_BACKGROUND);
                if (fillRight > barLeft) {
                    quad(pose, consumer, barLeft, BAR_TOP, fillRight, BAR_BOTTOM, -0.001F, barColor);
                }
            });
            drawString(collector, poseStack, state.percentText, aspect, 0.62D, 0.16F);
            drawString(collector, poseStack, state.countText, aspect, 0.82D, 0.10F);
        } else {
            drawString(collector, poseStack, state.emptyText, aspect, 0.50D, 0.11F);
        }

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }

    private static void quad(PoseStack.Pose pose, VertexConsumer consumer, float x0, float y0, float x1, float y1, float z, int color) {
        Matrix4f matrix = pose.pose();
        consumer.addVertex(matrix, x0, y1, z).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, x1, y1, z).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, x1, y0, z).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
        consumer.addVertex(matrix, x0, y0, z).setColor(color).setLight(LightCoordsUtil.FULL_BRIGHT);
    }

    private void drawString(SubmitNodeCollector collector, PoseStack poseStack, Component text, float aspect, double y, float size) {
        if (text.getString().isEmpty()) return;

        poseStack.pushPose();
        poseStack.translate(aspect / 2D, y, 0D);

        int len = font.width(text);
        float scale = size / 9F;
        float width = len * scale;
        if (width > aspect) {
            scale *= aspect / width;
            width = aspect;
        }
        if (width > 0.9F * aspect) {
            scale *= 0.9F;
        }

        poseStack.scale(scale, scale, 1F);
        collector.submitText(poseStack, -len / 2F, 0, text.getVisualOrderText(), true, Font.DisplayMode.NORMAL, LightCoordsUtil.FULL_BRIGHT, TEXT_COLOR, 0, 0);
        collector.submitText(poseStack, -len / 2F, 0, text.getVisualOrderText(), false, Font.DisplayMode.POLYGON_OFFSET, LightCoordsUtil.FULL_BRIGHT, TEXT_COLOR, 0, 0);
        poseStack.popPose();
    }
}
