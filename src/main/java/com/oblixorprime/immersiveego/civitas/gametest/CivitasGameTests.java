package com.oblixorprime.immersiveego.civitas.gametest;

import com.oblixorprime.immersiveego.civitas.ImmersiveEgoCivitas;
import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import com.oblixorprime.immersiveego.civitas.resident.CivitasResidentSavedData;
import com.oblixorprime.immersiveego.civitas.resident.LinkedResidentAssignmentService;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapter;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostAdapterRegistry;
import com.oblixorprime.immersiveego.civitas.resident.ResidentHostKey;
import com.oblixorprime.immersiveego.civitas.resident.ResidentRecord;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentApiContract;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentGateway;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentModuleLocator;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentResult;
import com.oblixorprime.immersiveego.civitas.resident.upstream.UpstreamResidentApiContract;
import com.oblixorprime.immersiveego.civitas.resident.upstream.UpstreamResidentHostAdapters;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ImmersiveEgoCivitas.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CivitasGameTests {
    private static final String EMPTY = "empty";

    private CivitasGameTests() {
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void serverRuntimeBoots(GameTestHelper helper) {
        helper.assertTrue(
                ImmersiveEgoCivitas.MOD_ID.equals("immersive_ego_civitas"),
                "mod id must match the registered GameTest namespace"
        );
        helper.succeed();
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void upstreamResidentApiContractMatchesInstalledRuntime(GameTestHelper helper) {
        var missingMembers = UpstreamResidentApiContract.missingMembers(
                Thread.currentThread().getContextClassLoader());

        helper.assertTrue(
                missingMembers.isEmpty(),
                "installed upstream resident API is missing members: " + missingMembers);
        helper.assertValueEqual(
                UpstreamResidentHostAdapters.createRegistry().adapters().size(),
                2,
                "MCA and MineColonies resident host adapters should be registered");
        helper.succeed();
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void mineColoniesAssignmentApiContractMatchesInstalledRuntime(GameTestHelper helper) {
        var missingMembers = MineColoniesAssignmentApiContract.missingMembers(
                Thread.currentThread().getContextClassLoader());

        helper.assertTrue(
                missingMembers.isEmpty(),
                "installed MineColonies assignment API is missing members: " + missingMembers);
        var missingLocatorTypes = MineColoniesAssignmentModuleLocator.missingRuntimeTypes(
                Thread.currentThread().getContextClassLoader());
        helper.assertTrue(
                missingLocatorTypes.isEmpty(),
                "installed MineColonies assignment locator types are missing: " + missingLocatorTypes);
        helper.succeed();
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void residentSavedDataKeepsCanonicalHostIndex(GameTestHelper helper) {
        CivitasResidentSavedData savedData = CivitasResidentSavedData.get(helper.getLevel());
        ResidentHostKey hostKey = new ResidentHostKey(
                CivitasAuthority.MCA_REBORN,
                "gametest:" + UUID.randomUUID());

        ResidentRecord first = savedData.getOrCreate(hostKey, helper.getLevel().getGameTime());
        ResidentRecord second = savedData.getOrCreate(hostKey, helper.getLevel().getGameTime() + 1L);

        helper.assertTrue(
                first.residentId().equals(second.residentId()),
                "same host key must resolve to one canonical CIVITAS resident");
        helper.assertTrue(
                savedData.find(hostKey).map(ResidentRecord::residentId).orElseThrow().equals(first.residentId()),
                "host index must find the canonical CIVITAS resident");
        helper.succeed();
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void residentSavedDataRoundTripsCanonicalHostIndex(GameTestHelper helper) {
        CivitasResidentSavedData savedData = new CivitasResidentSavedData();
        ResidentHostKey mcaHost = new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager:12");
        ResidentHostKey colonyHost = new ResidentHostKey(CivitasAuthority.MINECOLONIES, "citizen:44");

        ResidentRecord created = savedData.getOrCreate(mcaHost, 100L);
        savedData.linkHost(created.residentId(), colonyHost, 105L);

        CompoundTag serialized = savedData.save(new CompoundTag(), helper.getLevel().registryAccess());
        CivitasResidentSavedData loaded = CivitasResidentSavedData.load(serialized, helper.getLevel().registryAccess());

        helper.assertValueEqual(loaded.records().size(), 1, "one canonical resident should round-trip");
        helper.assertValueEqual(
                loaded.find(mcaHost).orElseThrow().residentId(),
                created.residentId(),
                "MCA host should resolve after NBT load");
        helper.assertValueEqual(
                loaded.find(colonyHost).orElseThrow().residentId(),
                created.residentId(),
                "MineColonies host should resolve after NBT load");
        helper.succeed();
    }

    @GameTest(template = EMPTY, timeoutTicks = 20)
    public static void linkedResidentAssignmentRequiresLinkedResident(GameTestHelper helper) {
        ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
        adapters.register(new GameTestMineColoniesAdapter());
        RecordingAssignmentGateway assignments = new RecordingAssignmentGateway();
        LinkedResidentAssignmentService service = new LinkedResidentAssignmentService(adapters, assignments);
        CivitasResidentSavedData savedData = new CivitasResidentSavedData();
        GameTestMineColoniesCitizen citizen = new GameTestMineColoniesCitizen("colony:17/citizen:42");
        Object targetHome = new Object();
        Object targetWork = new Object();

        MineColoniesAssignmentResult missingResident =
                service.assignHomeAndWork(savedData, citizen, targetHome, targetWork);
        helper.assertTrue(!missingResident.succeeded(), "unlinked citizen must fail closed");
        helper.assertTrue(!assignments.called, "unlinked citizen must not mutate MineColonies assignments");

        ResidentRecord record = savedData.getOrCreate(
                new ResidentHostKey(CivitasAuthority.MINECOLONIES, citizen.hostId()),
                100L);
        MineColoniesAssignmentResult missingMcaHost =
                service.assignHomeAndWork(savedData, citizen, targetHome, targetWork);
        helper.assertTrue(!missingMcaHost.succeeded(), "half-linked citizen must fail closed");
        helper.assertTrue(!assignments.called, "half-linked citizen must not mutate MineColonies assignments");

        savedData.linkHost(
                record.residentId(),
                new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:demo"),
                101L);
        MineColoniesAssignmentResult linked =
                service.assignHomeAndWork(savedData, citizen, targetHome, targetWork);
        helper.assertTrue(linked.succeeded(), "linked citizen should delegate to assignment gateway");
        helper.assertTrue(assignments.called, "linked citizen should reach assignment gateway");
        helper.succeed();
    }

    private record GameTestMineColoniesCitizen(String hostId) {
    }

    private static final class GameTestMineColoniesAdapter
            implements ResidentHostAdapter<GameTestMineColoniesCitizen> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MINECOLONIES;
        }

        @Override
        public Class<GameTestMineColoniesCitizen> hostType() {
            return GameTestMineColoniesCitizen.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(GameTestMineColoniesCitizen host) {
            return Optional.of(new ResidentHostKey(authority(), host.hostId()));
        }
    }

    private static final class RecordingAssignmentGateway implements MineColoniesAssignmentGateway {
        private boolean called;

        @Override
        public MineColoniesAssignmentResult assignHomeOnly(Object citizenData, Object targetHomeBuilding) {
            called = true;
            return MineColoniesAssignmentResult.applied(true, false, "recorded home assignment");
        }

        @Override
        public MineColoniesAssignmentResult assignHomeAndWork(
                Object citizenData,
                Object targetHomeBuilding,
                Object targetWorkBuilding) {
            called = true;
            return MineColoniesAssignmentResult.applied(true, true, "recorded home/work assignment");
        }
    }
}
