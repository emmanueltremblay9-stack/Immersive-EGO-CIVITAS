package com.oblixorprime.immersiveego.civitas.resident;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentGateway;
import com.oblixorprime.immersiveego.civitas.resident.upstream.MineColoniesAssignmentResult;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class LinkedResidentAssignmentServiceTest {
    private final ResidentHostAdapterRegistry adapters = new ResidentHostAdapterRegistry();
    private final RecordingAssignmentGateway assignments = new RecordingAssignmentGateway();
    private final LinkedResidentAssignmentService service =
            new LinkedResidentAssignmentService(adapters, assignments);
    private final FakeResidentDirectory residents = new FakeResidentDirectory();
    private final FakeMineColoniesCitizen citizen = new FakeMineColoniesCitizen("colony:17/citizen:42");

    LinkedResidentAssignmentServiceTest() {
        adapters.register(new FakeMineColoniesAdapter());
    }

    @Test
    void rejectsUnlinkedCitizenBeforeAssignmentMutation() {
        MineColoniesAssignmentResult result =
                service.assignHomeAndWork(residents, citizen, new Object(), new Object());

        assertFalse(result.succeeded());
        assertFalse(assignments.called);
        assertTrue(result.message().contains("not linked"));
    }

    @Test
    void rejectsHalfLinkedCitizenBeforeAssignmentMutation() {
        residents.add(new ResidentHostKey(CivitasAuthority.MINECOLONIES, citizen.hostId()));

        MineColoniesAssignmentResult result =
                service.assignHomeAndWork(residents, citizen, new Object(), new Object());

        assertFalse(result.succeeded());
        assertFalse(assignments.called);
        assertTrue(result.message().contains("missing an MCA Reborn host"));
    }

    @Test
    void delegatesLinkedCitizenToRepairableAssignmentGateway() {
        residents.add(
                new ResidentHostKey(CivitasAuthority.MINECOLONIES, citizen.hostId()),
                new ResidentHostKey(CivitasAuthority.MCA_REBORN, "villager_entity:demo"));
        Object targetHome = new Object();
        Object targetWork = new Object();

        MineColoniesAssignmentResult result =
                service.assignHomeAndWork(residents, citizen, targetHome, targetWork);

        assertTrue(result.succeeded());
        assertTrue(assignments.called);
        assertSame(citizen, assignments.citizenData);
        assertSame(targetHome, assignments.targetHomeBuilding);
        assertSame(targetWork, assignments.targetWorkBuilding);
    }

    private record FakeMineColoniesCitizen(String hostId) {
    }

    private static final class FakeResidentDirectory implements ResidentDirectory {
        private final Map<ResidentHostKey, ResidentRecord> recordsByHost = new LinkedHashMap<>();

        private void add(ResidentHostKey... hostKeys) {
            Map<CivitasAuthority, ResidentHostKey> hosts = new LinkedHashMap<>();
            for (ResidentHostKey hostKey : hostKeys) {
                hosts.put(hostKey.authority(), hostKey);
            }
            ResidentRecord record = new ResidentRecord(
                    UUID.randomUUID(),
                    100L,
                    100L,
                    hosts);
            for (ResidentHostKey hostKey : hostKeys) {
                recordsByHost.put(hostKey, record);
            }
        }

        @Override
        public Optional<ResidentRecord> find(ResidentHostKey hostKey) {
            return Optional.ofNullable(recordsByHost.get(hostKey));
        }
    }

    private static final class FakeMineColoniesAdapter
            implements ResidentHostAdapter<FakeMineColoniesCitizen> {
        @Override
        public CivitasAuthority authority() {
            return CivitasAuthority.MINECOLONIES;
        }

        @Override
        public Class<FakeMineColoniesCitizen> hostType() {
            return FakeMineColoniesCitizen.class;
        }

        @Override
        public Optional<ResidentHostKey> identify(FakeMineColoniesCitizen host) {
            return Optional.of(new ResidentHostKey(authority(), host.hostId()));
        }
    }

    private static final class RecordingAssignmentGateway implements MineColoniesAssignmentGateway {
        private boolean called;
        private Object citizenData;
        private Object targetHomeBuilding;
        private Object targetWorkBuilding;

        @Override
        public MineColoniesAssignmentResult assignHomeOnly(Object citizenData, Object targetHomeBuilding) {
            called = true;
            this.citizenData = citizenData;
            this.targetHomeBuilding = targetHomeBuilding;
            return MineColoniesAssignmentResult.applied(true, false, "recorded home assignment");
        }

        @Override
        public MineColoniesAssignmentResult assignHomeAndWork(
                Object citizenData,
                Object targetHomeBuilding,
                Object targetWorkBuilding) {
            called = true;
            this.citizenData = citizenData;
            this.targetHomeBuilding = targetHomeBuilding;
            this.targetWorkBuilding = targetWorkBuilding;
            return MineColoniesAssignmentResult.applied(true, true, "recorded home/work assignment");
        }
    }
}
