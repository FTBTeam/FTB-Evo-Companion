package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import dev.ftb.mods.ftbevolutioncompanion.FTBEvolutionCompanion;
import dev.ftb.mods.ftbevolutioncompanion.content.CompanionContent;
import dev.ftb.mods.ftblibrary.icon.ItemIcon;
import dev.ftb.mods.ftblibrary.util.TooltipList;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.quest.TeamData;
import dev.ftb.mods.ftbquests.quest.task.Task;
import dev.ftb.mods.ftbquests.quest.task.TaskClient;
import dev.ftb.mods.ftbquests.quest.task.TaskType;
import dev.ftb.mods.ftbquests.quest.task.TaskTypes;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class LaunchTask extends Task {
    public static TaskType TYPE;

    public LaunchTask(long id, Quest quest) {
        super(id, quest);
    }

    public static void register() {
        TYPE = TaskTypes.register(FTBEvolutionCompanion.id("launch"), LaunchTask::new,
                () -> ItemIcon.ofItem(CompanionContent.SKYLINE_ITEM.get()));
    }

    @Override
    public TaskType getType() {
        return TYPE;
    }

    @Override
    public TaskClient client() {
        return TaskClient.NoOp.INSTANCE;
    }

    @Override
    public boolean checkOnLogin() {
        return false;
    }

    @Override
    public void addMouseOverText(TooltipList list, TeamData teamData) {
        list.add(Component.translatable("ftbevolutioncompanion.ftb_skyline.launch_task.tooltip").withStyle(ChatFormatting.GRAY));
    }
}
