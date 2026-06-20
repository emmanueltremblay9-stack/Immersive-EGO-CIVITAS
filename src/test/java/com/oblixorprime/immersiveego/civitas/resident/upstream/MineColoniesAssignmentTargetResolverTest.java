package com.oblixorprime.immersiveego.civitas.resident.upstream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class MineColoniesAssignmentTargetResolverTest {
    private final FakeLevel level = new FakeLevel();
    private final FakeColonyManager colonyManager = new FakeColonyManager();
    private final MineColoniesAssignmentTargetResolver resolver =
            new MineColoniesAssignmentTargetResolver(() -> Optional.of(colonyManager));

    @Test
    void resolvesCitizenHomeAndWorkFromColonyManager() {
        FakeColony colony = colonyManager.addColony(17);
        FakeCitizenData citizen = colony.citizens.add(42);
        FakeBuilding home = colonyManager.addBuilding(new FakePos(1, 2, 3), colony);
        FakeBuilding work = colonyManager.addBuilding(new FakePos(4, 5, 6), colony);

        MineColoniesAssignmentTarget target = resolver.resolveHomeAndWork(
                level,
                17,
                42,
                home.position,
                work.position);

        assertTrue(target.resolved());
        assertSame(citizen, target.citizenData());
        assertSame(home, target.targetHomeBuilding());
        assertSame(work, target.targetWorkBuilding());
    }

    @Test
    void rejectsMissingCitizenBeforeBuildingLookupCanMutate() {
        FakeColony colony = colonyManager.addColony(17);
        FakeBuilding home = colonyManager.addBuilding(new FakePos(1, 2, 3), colony);

        MineColoniesAssignmentTarget target = resolver.resolveHomeOnly(
                level,
                17,
                42,
                home.position);

        assertFalse(target.resolved());
        assertTrue(target.message().contains("citizen 42"));
    }

    @Test
    void rejectsBuildingOwnedByAnotherColony() {
        FakeColony requestedColony = colonyManager.addColony(17);
        requestedColony.citizens.add(42);
        FakeColony otherColony = colonyManager.addColony(18);
        FakeBuilding otherHome = colonyManager.addBuilding(new FakePos(1, 2, 3), otherColony);

        MineColoniesAssignmentTarget target = resolver.resolveHomeOnly(
                level,
                17,
                42,
                otherHome.position);

        assertFalse(target.resolved());
        assertTrue(target.message().contains("belongs to colony 18"));
    }

    @Test
    void rejectsUnavailableColonyManager() {
        MineColoniesAssignmentTargetResolver unavailable =
                new MineColoniesAssignmentTargetResolver(Optional::empty);

        MineColoniesAssignmentTarget target = unavailable.resolveHomeOnly(
                level,
                17,
                42,
                new FakePos(1, 2, 3));

        assertFalse(target.resolved());
        assertTrue(target.message().contains("unavailable"));
    }

    private static final class FakeLevel {
    }

    private record FakePos(int x, int y, int z) {
    }

    private static final class FakeColonyManager {
        private final Map<Integer, FakeColony> colonies = new LinkedHashMap<>();
        private final Map<FakePos, FakeBuilding> buildings = new LinkedHashMap<>();

        private FakeColony addColony(int id) {
            FakeColony colony = new FakeColony(id);
            colonies.put(id, colony);
            return colony;
        }

        private FakeBuilding addBuilding(FakePos position, FakeColony colony) {
            FakeBuilding building = new FakeBuilding(position, colony);
            buildings.put(position, building);
            return building;
        }

        public FakeColony getColonyByWorld(int id, FakeLevel level) {
            return colonies.get(id);
        }

        public FakeBuilding getBuilding(FakeLevel level, FakePos position) {
            return buildings.get(position);
        }
    }

    private static final class FakeColony {
        private final int id;
        private final FakeCitizenManager citizens = new FakeCitizenManager();

        private FakeColony(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        public FakeCitizenManager getCitizenManager() {
            return citizens;
        }
    }

    private static final class FakeCitizenManager {
        private final Map<Integer, FakeCitizenData> citizens = new LinkedHashMap<>();

        private FakeCitizenData add(int id) {
            FakeCitizenData citizen = new FakeCitizenData(id);
            citizens.put(id, citizen);
            return citizen;
        }

        public FakeCitizenData getCivilian(int id) {
            return citizens.get(id);
        }
    }

    private record FakeCitizenData(int id) {
    }

    private record FakeBuilding(FakePos position, FakeColony colony) {
        public FakeColony getColony() {
            return colony;
        }
    }
}
