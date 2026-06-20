package com.oblixorprime.immersiveego.civitas.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oblixorprime.immersiveego.civitas.resident.CivitasResidentSavedData;
import com.oblixorprime.immersiveego.civitas.resident.LinkedResidentAssignmentService;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentResult;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentTarget;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentTargetResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class CivitasServerCommands {
    private static final int REQUIRED_PERMISSION_LEVEL = 2;

    private final MineColoniesAssignmentTargetResolver targetResolver;
    private final LinkedResidentAssignmentService assignmentService;

    public CivitasServerCommands() {
        this(new MineColoniesAssignmentTargetResolver(), new LinkedResidentAssignmentService());
    }

    CivitasServerCommands(
            MineColoniesAssignmentTargetResolver targetResolver,
            LinkedResidentAssignmentService assignmentService) {
        this.targetResolver = targetResolver;
        this.assignmentService = assignmentService;
    }

    public static void register(RegisterCommandsEvent event) {
        new CivitasServerCommands().register(event.getDispatcher());
    }

    void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("civitas")
                .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .then(Commands.literal("assign_minecolonies_home")
                        .then(Commands.argument("colonyId", IntegerArgumentType.integer(1))
                                .then(Commands.argument("citizenId", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("home", BlockPosArgument.blockPos())
                                                .executes(this::assignHomeOnly)))))
                .then(Commands.literal("assign_minecolonies_home_work")
                        .then(Commands.argument("colonyId", IntegerArgumentType.integer(1))
                                .then(Commands.argument("citizenId", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("home", BlockPosArgument.blockPos())
                                                .then(Commands.argument("work", BlockPosArgument.blockPos())
                                                        .executes(this::assignHomeAndWork)))))));
    }

    private int assignHomeOnly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        MineColoniesAssignmentTarget target = targetResolver.resolveHomeOnly(
                level,
                IntegerArgumentType.getInteger(context, "colonyId"),
                IntegerArgumentType.getInteger(context, "citizenId"),
                blockPos(context, "home"));
        if (!target.resolved()) {
            return fail(source, target.message());
        }

        MineColoniesAssignmentResult result = assignmentService.assignHomeOnly(
                CivitasResidentSavedData.get(level),
                target.citizenData(),
                target.targetHomeBuilding());
        return report(source, result);
    }

    private int assignHomeAndWork(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        MineColoniesAssignmentTarget target = targetResolver.resolveHomeAndWork(
                level,
                IntegerArgumentType.getInteger(context, "colonyId"),
                IntegerArgumentType.getInteger(context, "citizenId"),
                blockPos(context, "home"),
                blockPos(context, "work"));
        if (!target.resolved()) {
            return fail(source, target.message());
        }

        MineColoniesAssignmentResult result = assignmentService.assignHomeAndWork(
                CivitasResidentSavedData.get(level),
                target.citizenData(),
                target.targetHomeBuilding(),
                target.targetWorkBuilding());
        return report(source, result);
    }

    private static BlockPos blockPos(CommandContext<CommandSourceStack> context, String name)
            throws CommandSyntaxException {
        return BlockPosArgument.getLoadedBlockPos(context, name);
    }

    private static int report(CommandSourceStack source, MineColoniesAssignmentResult result) {
        if (!result.succeeded()) {
            return fail(source, result.message());
        }

        source.sendSuccess(
                () -> Component.literal("CIVITAS assignment applied: " + result.message()),
                true);
        return Command.SINGLE_SUCCESS;
    }

    private static int fail(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal("CIVITAS assignment failed: " + message));
        return 0;
    }
}
