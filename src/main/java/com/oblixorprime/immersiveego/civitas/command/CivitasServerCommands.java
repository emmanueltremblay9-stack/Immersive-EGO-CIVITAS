package com.oblixorprime.immersiveego.civitas.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oblixorprime.immersiveego.civitas.resident.CivitasResidentSavedData;
import com.oblixorprime.immersiveego.civitas.resident.LinkedResidentAssignmentService;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapterRegistry;
import com.oblixorprime.immersiveego.civitas.resident.ResidentIdentityService;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecruitmentResult;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecruitmentService;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentCoordinator;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentResult;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentTarget;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentTargetResolver;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesCitizenTarget;
import com.oblixorprime.immersiveego.civitas.resident.upstream.UpstreamResidentHostAdapters;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import java.util.UUID;

public final class CivitasServerCommands {
    private static final int REQUIRED_PERMISSION_LEVEL = 2;

    private final MineColoniesAssignmentTargetResolver targetResolver;
    private final LinkedResidentAssignmentService assignmentService;
    private final ResidentHostAdapterRegistry residentAdapters;

    public CivitasServerCommands() {
        this(new MineColoniesAssignmentTargetResolver(), UpstreamResidentHostAdapters.createRegistry());
    }

    private CivitasServerCommands(
            MineColoniesAssignmentTargetResolver targetResolver,
            ResidentHostAdapterRegistry residentAdapters) {
        this(
                targetResolver,
                new LinkedResidentAssignmentService(residentAdapters, new MineColoniesAssignmentCoordinator()),
                residentAdapters);
    }

    CivitasServerCommands(
            MineColoniesAssignmentTargetResolver targetResolver,
            LinkedResidentAssignmentService assignmentService,
            ResidentHostAdapterRegistry residentAdapters) {
        this.targetResolver = targetResolver;
        this.assignmentService = assignmentService;
        this.residentAdapters = residentAdapters;
    }

    public static void register(RegisterCommandsEvent event) {
        new CivitasServerCommands().register(event.getDispatcher());
    }

    void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("civitas")
                .requires(source -> source.hasPermission(REQUIRED_PERMISSION_LEVEL))
                .then(Commands.literal("link_mca_minecolonies")
                        .then(Commands.argument("mcaVillager", EntityArgument.entity())
                                .then(Commands.argument("colonyId", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("citizenId", IntegerArgumentType.integer(1))
                                                .executes(this::linkMcaMineColonies)))))
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

    private int linkMcaMineColonies(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();
        Entity mcaVillager = EntityArgument.getEntity(context, "mcaVillager");
        MineColoniesCitizenTarget target = targetResolver.resolveCitizen(
                level,
                IntegerArgumentType.getInteger(context, "colonyId"),
                IntegerArgumentType.getInteger(context, "citizenId"));
        if (!target.resolved()) {
            return failLink(source, target.message());
        }

        CivitasResidentSavedData residents = CivitasResidentSavedData.get(level);
        ResidentRecruitmentService recruitment = new ResidentRecruitmentService(
                new ResidentIdentityService(residents, residentAdapters));
        try {
            ResidentRecruitmentResult result = recruitment.recruitMcaIntoColony(
                    mcaVillager,
                    target.citizenData(),
                    level.getGameTime(),
                    UUID::randomUUID);
            source.sendSuccess(
                    () -> Component.literal("CIVITAS resident linked: "
                            + result.resident().residentId()
                            + " mca="
                            + result.mcaHost().hostId()
                            + " minecolonies="
                            + result.mineColoniesHost().hostId()),
                    true);
            return Command.SINGLE_SUCCESS;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            return failLink(source, exception.getMessage());
        }
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
            return failAssignment(source, target.message());
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
            return failAssignment(source, target.message());
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
            return failAssignment(source, result.message());
        }

        source.sendSuccess(
                () -> Component.literal("CIVITAS assignment applied: " + result.message()),
                true);
        return Command.SINGLE_SUCCESS;
    }

    private static int failAssignment(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal("CIVITAS assignment failed: " + message));
        return 0;
    }

    private static int failLink(CommandSourceStack source, String message) {
        source.sendFailure(Component.literal("CIVITAS link failed: " + message));
        return 0;
    }
}
