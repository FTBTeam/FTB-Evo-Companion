package dev.ftb.mods.ftbevolutioncompanion.challenge;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.jspecify.annotations.Nullable;

public final class ChallengeBoardCommand {
    private static final String LANG = "ftbevolutioncompanion.challenge_board.";
    private static final double REACH = 12.0D;

    private ChallengeBoardCommand() {
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("challengeboard")
                .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("size")
                        .then(Commands.argument("width", IntegerArgumentType.integer(1, ChallengeBoardBlockEntity.MAX_SIZE))
                                .then(Commands.argument("height", IntegerArgumentType.integer(1, ChallengeBoardBlockEntity.MAX_SIZE))
                                        .executes(ChallengeBoardCommand::size))))
                .then(Commands.literal("rank")
                        .then(Commands.argument("rank", IntegerArgumentType.integer(1, 64))
                                .executes(ChallengeBoardCommand::rank))));
    }

    private static int size(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ChallengeBoardBlockEntity board = targetBoard(context);
        if (board == null) return 0;
        int width = IntegerArgumentType.getInteger(context, "width");
        int height = IntegerArgumentType.getInteger(context, "height");
        BlockPos blocked = ChallengeBoardBlock.resize(context.getSource().getLevel(), board, width, height);
        if (blocked != null) {
            context.getSource().sendFailure(Component.translatable(LANG + "blocked", blocked.getX(), blocked.getY(), blocked.getZ()));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable(LANG + "resized", width, height), true);
        return 1;
    }

    private static int rank(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ChallengeBoardBlockEntity board = targetBoard(context);
        if (board == null) return 0;
        int rank = IntegerArgumentType.getInteger(context, "rank");
        board.setRank(rank);
        context.getSource().sendSuccess(() -> Component.translatable(LANG + "rank_set", rank), true);
        return 1;
    }

    @Nullable
    private static ChallengeBoardBlockEntity targetBoard(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerLevel level = context.getSource().getLevel();
        HitResult hit = player.pick(REACH, 1.0F, false);
        ChallengeBoardBlockEntity board = hit instanceof BlockHitResult blockHit
                ? ChallengeBoardBlock.coreAt(level, blockHit.getBlockPos()).orElse(null)
                : null;
        if (board == null) {
            context.getSource().sendFailure(Component.translatable(LANG + "no_board"));
        }
        return board;
    }
}
