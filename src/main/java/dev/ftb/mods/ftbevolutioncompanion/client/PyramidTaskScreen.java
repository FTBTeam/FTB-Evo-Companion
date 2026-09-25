package dev.ftb.mods.ftbevolutioncompanion.client;

import dev.ftb.mods.ftbevolutioncompanion.pyramid.SkylineBlockEntity;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.LaunchTask;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.PyramidQuests;
import dev.ftb.mods.ftbevolutioncompanion.pyramid.network.PyramidPayloads;
import dev.ftb.mods.ftblibrary.client.gui.WidgetType;
import dev.ftb.mods.ftblibrary.client.gui.input.MouseButton;
import dev.ftb.mods.ftblibrary.client.gui.screens.AbstractGroupedButtonListScreen;
import dev.ftb.mods.ftblibrary.client.gui.theme.Theme;
import dev.ftb.mods.ftblibrary.client.gui.widget.Panel;
import dev.ftb.mods.ftblibrary.client.gui.widget.SimpleTextButton;
import dev.ftb.mods.ftblibrary.client.gui.widget.TextField;
import dev.ftb.mods.ftblibrary.client.icon.IconHelper;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.client.ClientQuestFile;
import dev.ftb.mods.ftbquests.client.gui.FTBQuestsTheme;
import dev.ftb.mods.ftbquests.quest.Chapter;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PyramidTaskScreen extends AbstractGroupedButtonListScreen<Quest, Task> {
    private static final String LANG = "ftbevolutioncompanion.ftb_skyline.";
    private static final double MAX_DISTANCE = 16.0D;
    private static final Color4I ACTIVE = Color4I.rgb(0x55FF55).withAlpha(40);
    private static final Color4I BAR_BACKGROUND = Color4I.rgb(0x1A1A1A);
    private static final Color4I BAR_FILL = Color4I.rgb(0xFFAA00);
    private static final Color4I BAR_DONE = Color4I.rgb(0x55FF55);

    private final BlockPos pos;
    private final long chapterId;
    private List<Long> signature = List.of();

    public PyramidTaskScreen(BlockPos pos, long chapterId) {
        super(Component.translatable(LANG + "screen_title", chapterTitle(chapterId)));
        this.pos = pos;
        this.chapterId = chapterId;
        setHasSearchBox(false);
    }

    private static Component chapterTitle(long chapterId) {
        Chapter chapter = ClientQuestFile.exists() ? ClientQuestFile.getInstance().getChapter(chapterId) : null;
        return chapter == null ? Component.translatable(LANG + "unknown_chapter") : chapter.getTitle();
    }

    private static TeamData data() {
        return ClientQuestFile.exists() ? ClientQuestFile.getInstance().selfTeamData : TeamData.NONE;
    }

    private long activeTaskId() {
        var level = Minecraft.getInstance().level;
        return level != null && level.getBlockEntity(pos) instanceof SkylineBlockEntity machine ? machine.getActiveTaskId() : 0L;
    }

    private boolean machineLaunching() {
        var level = Minecraft.getInstance().level;
        return level != null && level.getBlockEntity(pos) instanceof SkylineBlockEntity machine && machine.isLaunching();
    }

    private List<Quest> availableQuests() {
        List<Quest> quests = new ArrayList<>();
        if (!ClientQuestFile.exists()) return quests;
        Chapter chapter = ClientQuestFile.getInstance().getChapter(chapterId);
        if (chapter == null) return quests;
        TeamData data = data();
        for (Quest quest : chapter.getQuests()) {
            if (PyramidQuests.isAvailable(data, quest)) {
                quests.add(quest);
            }
        }
        return quests;
    }

    private List<Long> computeSignature() {
        List<Long> ids = new ArrayList<>();
        for (Quest quest : availableQuests()) {
            ids.add(quest.id);
            for (Task task : quest.getTasksAsList()) {
                ids.add(data().isCompleted(task) ? task.id : -task.id);
            }
        }
        return ids;
    }

    @Override
    protected List<GroupData<Quest, Task>> buildGroupData() {
        List<GroupData<Quest, Task>> groups = new ArrayList<>();
        for (Quest quest : availableQuests()) {
            groups.add(new GroupData<>(quest, false, quest.getTitle(), quest.getTasksAsList()));
        }
        signature = computeSignature();
        return groups;
    }

    @Override
    public void addButtons(Panel panel) {
        super.addButtons(panel);
        if (getGroupData().isEmpty()) {
            panel.add(new TextField(panel).setText(Component.translatable(LANG + "nothing_available").withStyle(ChatFormatting.GRAY)));
        }
    }

    @Override
    protected RowPanel createRowPanel(Panel panel, Task task) {
        return new TaskRow(panel, task);
    }

    @Override
    public boolean onInit() {
        setWidth(Mth.clamp(320, 220, getWindow().getGuiScaledWidth() * 4 / 5));
        setHeight(getWindow().getGuiScaledHeight() * 4 / 5);
        return true;
    }

    @Override
    public Theme getTheme() {
        return FTBQuestsTheme.INSTANCE;
    }

    @Override
    public void alignWidgets() {
        super.alignWidgets();
        mainPanel.getWidgets().forEach(widget -> {
            if (widget instanceof TaskRow row) {
                row.alignWidgets();
            }
        });
    }

    @Override
    public void tick() {
        super.tick();
        var player = Minecraft.getInstance().player;
        var level = Minecraft.getInstance().level;
        if (player == null || level == null || !(level.getBlockEntity(pos) instanceof SkylineBlockEntity)
                || player.position().distanceToSqr(pos.getCenter()) > MAX_DISTANCE * MAX_DISTANCE) {
            closeGui(false);
            return;
        }
        if (!Objects.equals(signature, computeSignature())) {
            rebuildGroupData();
            refreshWidgets();
        }
    }

    @Override
    protected void doCancel() {
        closeGui(false);
    }

    private class TaskRow extends RowPanel {
        private final boolean selectable;
        private SimpleTextButton launchButton;

        TaskRow(Panel panel, Task task) {
            super(panel, task);
            this.selectable = PyramidQuests.isDeliverable(task);
            setHeight(22);
        }

        @Override
        public void addWidgets() {
            if (value instanceof LaunchTask) {
                launchButton = new SimpleTextButton(this, Component.translatable(LANG + "launch"), ItemIcon.ofItem(Items.FIREWORK_ROCKET)) {
                    @Override
                    public void onClicked(MouseButton button) {
                        playClickSound();
                        ClientPacketDistributor.sendToServer(new PyramidPayloads.LaunchPyramid(pos, value.getQuest().id));
                    }

                    @Override
                    public WidgetType getWidgetType() {
                        return canLaunch() ? super.getWidgetType() : WidgetType.DISABLED;
                    }

                    @Override
                    public void addMouseOverText(TooltipList list) {
                        list.add(Component.translatable(canLaunch() ? LANG + "launch.ready" : LANG + "launch.not_ready"));
                    }
                };
                add(launchButton);
            }
        }

        private boolean canLaunch() {
            return !machineLaunching() && PyramidQuests.isReadyToLaunch(data(), value.getQuest());
        }

        @Override
        public void alignWidgets() {
            if (launchButton != null) {
                int right = width - 4 - (scrollBar.shouldDraw() ? scrollBar.width : 0);
                launchButton.setPos(right - launchButton.width, (height - launchButton.height) / 2);
            }
        }

        @Override
        public boolean mousePressed(MouseButton button) {
            if (super.mousePressed(button)) return true;
            if (!isMouseOver() || !button.isLeft() || !selectable) return false;
            TeamData data = data();
            if (data.isCompleted(value) || value.id == activeTaskId()) return true;
            playClickSound();
            ClientPacketDistributor.sendToServer(new PyramidPayloads.SelectPyramidTask(pos, value.id));
            return true;
        }

        @Override
        public void addMouseOverText(TooltipList list) {
            super.addMouseOverText(list);
            if (launchButton != null && launchButton.isMouseOver()) return;
            list.add(value.getTitle());
            if (selectable && !data().isCompleted(value)) {
                list.add(Component.translatable(value.id == activeTaskId() ? LANG + "active" : LANG + "select").withStyle(ChatFormatting.GRAY));
            } else if (!selectable && !(value instanceof LaunchTask)) {
                list.add(Component.translatable(LANG + "not_deliverable").withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        @Override
        public void drawBackground(GuiGraphicsExtractor graphics, Theme theme, int x, int y, int w, int h) {
            super.drawBackground(graphics, theme, x, y, w, h);
            TeamData data = data();
            boolean active = value.id == activeTaskId();
            boolean done = data.isCompleted(value);

            if (active) {
                IconHelper.renderIcon(ACTIVE, graphics, x, y, w, h);
            }

            IconHelper.renderIconStatic(value.getIcon(), graphics, x + 4, y + (h - 16) / 2, 16, 16);

            Component title = value.getTitle().copy().withStyle(done ? ChatFormatting.GREEN : active ? ChatFormatting.YELLOW : ChatFormatting.WHITE);
            int textX = x + 24;
            int textRight = launchButton != null ? x + launchButton.posX - 4 : x + w - 110;
            theme.drawString(graphics, theme.trimStringToWidth(title, Math.max(20, textRight - textX)), textX, y + (h - theme.getFontHeight()) / 2 + 1, Theme.SHADOW);

            if (value instanceof LaunchTask || value.hideProgressNumbers() && !selectable) {
                if (done) {
                    theme.drawString(graphics, Component.translatable(LANG + "done").withStyle(ChatFormatting.GREEN), x + w - 60, y + (h - theme.getFontHeight()) / 2 + 1, Theme.SHADOW);
                }
                graphics.horizontalLine(x, x + w, y + h, 0x40808080);
                return;
            }

            long max = Math.max(1L, value.getMaxProgress());
            long progress = Math.min(data.getProgress(value), max);
            int barW = 96;
            int barX = x + w - barW - 6 - (scrollBar.shouldDraw() ? scrollBar.width : 0);
            int barY = y + h - 7;
            IconHelper.renderIcon(BAR_BACKGROUND, graphics, barX, barY, barW, 3);
            int fill = (int) (barW * (double) progress / max);
            if (fill > 0) {
                IconHelper.renderIcon(done ? BAR_DONE : BAR_FILL, graphics, barX, barY, fill, 3);
            }
            Component amount = Component.translatable(LANG + "progress", value.formatProgress(data, progress), value.formatMaxProgress())
                    .withStyle(done ? ChatFormatting.GREEN : ChatFormatting.GRAY);
            theme.drawString(graphics, amount, barX + barW - theme.getStringWidth(amount), y + 3, Theme.SHADOW);
            graphics.horizontalLine(x, x + w, y + h, 0x40808080);
        }
    }
}
