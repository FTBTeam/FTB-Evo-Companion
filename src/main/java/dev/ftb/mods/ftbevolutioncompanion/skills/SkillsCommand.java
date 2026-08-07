package dev.ftb.mods.ftbevolutioncompanion.skills;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.Arrays;

public final class SkillsCommand {
    private SkillsCommand() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ftbskills")
                .then(Commands.literal("toggle")
                        .then(Commands.argument("skill", StringArgumentType.word())
                                .suggests((context, suggestions) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(SkillToggles.Toggle.values()).map(SkillToggles.Toggle::key),
                                        suggestions))
                                .executes(context -> {
                                    ServerPlayer player = context.getSource().getPlayerOrException();
                                    String key = StringArgumentType.getString(context, "skill");
                                    for (SkillToggles.Toggle toggle : SkillToggles.Toggle.values()) {
                                        if (toggle.key().equals(key)) {
                                            SkillsAbilities.handleToggle(player, toggle);
                                            return 1;
                                        }
                                    }
                                    context.getSource().sendFailure(Component.translatable(
                                            "ftbevolutioncompanion.skills.unknown_toggle", key));
                                    return 0;
                                }))));
    }
}
