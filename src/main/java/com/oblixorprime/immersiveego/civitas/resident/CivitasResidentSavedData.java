package com.oblixorprime.immersiveego.civitas.resident;

import com.oblixorprime.immersiveego.civitas.ImmersiveEgoCivitas;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public final class CivitasResidentSavedData extends SavedData implements ResidentDirectory {
    public static final String FILE_ID = ImmersiveEgoCivitas.MOD_ID + "_residents";

    private static final String TAG_SCHEMA = "schema";
    private static final String TAG_RECORDS = "records";
    private static final String TAG_ID = "id";
    private static final String TAG_CREATED_GAME_TIME = "createdGameTime";
    private static final String TAG_UPDATED_GAME_TIME = "updatedGameTime";
    private static final String TAG_HOSTS = "hosts";
    private static final String TAG_AUTHORITY = "authority";
    private static final String TAG_HOST_ID = "hostId";
    private static final int SCHEMA_VERSION = 1;
    private static final Factory<CivitasResidentSavedData> FACTORY = new Factory<>(
            CivitasResidentSavedData::new,
            CivitasResidentSavedData::load);

    private final ResidentRegistry registry = new ResidentRegistry();

    public static CivitasResidentSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, FILE_ID);
    }

    public Collection<ResidentRecord> records() {
        return registry.records();
    }

    @Override
    public Optional<ResidentRecord> find(ResidentHostKey hostKey) {
        return registry.find(hostKey);
    }

    public ResidentRecord getOrCreate(ResidentHostKey hostKey, long gameTime) {
        ResidentRecord record = registry.getOrCreate(hostKey, gameTime, UUID::randomUUID);
        setDirty();
        return record;
    }

    public ResidentRecord linkHost(UUID residentId, ResidentHostKey hostKey, long gameTime) {
        ResidentRecord record = registry.linkHost(residentId, hostKey, gameTime);
        setDirty();
        return record;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putInt(TAG_SCHEMA, SCHEMA_VERSION);

        ListTag recordTags = new ListTag();
        registry.records().stream()
                .map(CivitasResidentSavedData::saveRecord)
                .forEach(recordTags::add);
        tag.put(TAG_RECORDS, recordTags);
        return tag;
    }

    public static CivitasResidentSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        CivitasResidentSavedData savedData = new CivitasResidentSavedData();
        ListTag recordTags = tag.getList(TAG_RECORDS, Tag.TAG_COMPOUND);
        for (int index = 0; index < recordTags.size(); index++) {
            savedData.registry.register(loadRecord(recordTags.getCompound(index)));
        }
        return savedData;
    }

    private static CompoundTag saveRecord(ResidentRecord record) {
        CompoundTag tag = new CompoundTag();
        tag.putUUID(TAG_ID, record.residentId());
        tag.putLong(TAG_CREATED_GAME_TIME, record.createdGameTime());
        tag.putLong(TAG_UPDATED_GAME_TIME, record.updatedGameTime());

        ListTag hostTags = new ListTag();
        record.hosts().values().stream()
                .sorted((left, right) -> left.authority().serializedId().compareTo(right.authority().serializedId()))
                .map(CivitasResidentSavedData::saveHost)
                .forEach(hostTags::add);
        tag.put(TAG_HOSTS, hostTags);
        return tag;
    }

    private static ResidentRecord loadRecord(CompoundTag tag) {
        if (!tag.hasUUID(TAG_ID)) {
            throw new IllegalArgumentException("Resident record is missing id");
        }

        EnumMap<CivitasAuthority, ResidentHostKey> hosts = new EnumMap<>(CivitasAuthority.class);
        ListTag hostTags = tag.getList(TAG_HOSTS, Tag.TAG_COMPOUND);
        for (int index = 0; index < hostTags.size(); index++) {
            ResidentHostKey hostKey = loadHost(hostTags.getCompound(index));
            hosts.put(hostKey.authority(), hostKey);
        }

        return new ResidentRecord(
                tag.getUUID(TAG_ID),
                tag.getLong(TAG_CREATED_GAME_TIME),
                tag.getLong(TAG_UPDATED_GAME_TIME),
                hosts);
    }

    private static CompoundTag saveHost(ResidentHostKey hostKey) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_AUTHORITY, hostKey.authority().serializedId());
        tag.putString(TAG_HOST_ID, hostKey.hostId());
        return tag;
    }

    private static ResidentHostKey loadHost(CompoundTag tag) {
        CivitasAuthority authority = CivitasAuthority.bySerializedId(tag.getString(TAG_AUTHORITY))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown CIVITAS authority '" + tag.getString(TAG_AUTHORITY) + "'"));
        return new ResidentHostKey(authority, tag.getString(TAG_HOST_ID));
    }
}
