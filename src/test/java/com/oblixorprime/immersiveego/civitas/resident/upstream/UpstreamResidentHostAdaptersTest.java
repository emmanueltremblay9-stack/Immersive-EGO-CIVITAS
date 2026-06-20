package com.oblixorprime.immersiveego.civitas.resident.upstream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.oblixorprime.immersiveego.civitas.resident.CivitasAuthority;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UpstreamResidentHostAdaptersTest {
    @Test
    void mcaAdapterIdentifiesVillagerEntityUuid() {
        UUID entityId = UUID.fromString("52cc5adc-a51a-4c98-bc21-3d623f08f8a5");
        var adapter = new McaVillagerResidentHostAdapter(Set.of(FakeMcaVillager.class.getName()));

        var hostKey = adapter.identify(new FakeMcaVillager(entityId)).orElseThrow();

        assertEquals(CivitasAuthority.MCA_REBORN, hostKey.authority());
        assertEquals("villager_entity:" + entityId, hostKey.hostId());
    }

    @Test
    void mineColoniesAdapterIdentifiesCitizenDataAndEntity() {
        var adapter = new MineColoniesCitizenResidentHostAdapter(
                Set.of(FakeCitizenEntity.class.getName()),
                Set.of(FakeCitizenData.class.getName()));
        var colony = new FakeColony(17);
        var data = new FakeCitizenData(42, colony);

        var dataKey = adapter.identify(data).orElseThrow();
        var entityDataKey = adapter.identify(new FakeCitizenEntity(data, 0, new FakeColonyHandler(0)))
                .orElseThrow();
        var entityFallbackKey = adapter.identify(new FakeCitizenEntity(null, 42, new FakeColonyHandler(17)))
                .orElseThrow();

        assertEquals(CivitasAuthority.MINECOLONIES, dataKey.authority());
        assertEquals("colony:17/citizen:42", dataKey.hostId());
        assertEquals(dataKey, entityDataKey);
        assertEquals(dataKey, entityFallbackKey);
    }

    @Test
    void mineColoniesAdapterRejectsUnsupportedAndUnregisteredHosts() {
        var adapter = new MineColoniesCitizenResidentHostAdapter(
                Set.of(FakeCitizenEntity.class.getName()),
                Set.of(FakeCitizenData.class.getName()));

        assertTrue(adapter.identify(new Object()).isEmpty());
        assertTrue(adapter.identify(new FakeCitizenData(0, new FakeColony(17))).isEmpty());
        assertTrue(adapter.identify(new FakeCitizenData(42, new FakeColony(0))).isEmpty());
        assertTrue(adapter.identify(new FakeCitizenEntity(null, 0, new FakeColonyHandler(17))).isEmpty());
    }

    private record FakeMcaVillager(UUID getUUID) {
    }

    private record FakeCitizenData(int getId, FakeColony getColony) {
    }

    private record FakeColony(int getID) {
    }

    private record FakeCitizenEntity(
            FakeCitizenData getCitizenData,
            int getCivilianID,
            FakeColonyHandler getCitizenColonyHandler) {
    }

    private record FakeColonyHandler(int getColonyId) {
    }
}
