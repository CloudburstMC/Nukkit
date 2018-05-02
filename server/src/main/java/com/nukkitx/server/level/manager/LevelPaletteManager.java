package com.nukkitx.server.level.manager;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.block.NukkitBlockStateBuilder;
import com.nukkitx.server.metadata.MetadataSerializers;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LevelPaletteManager {
    private static final int RUNTIMEID_TABLE_CAPACITY = 4467;
    private final TIntObjectMap<BlockState> runtimeId2BlockState = new TIntObjectHashMap<>(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1);
    private final TObjectIntMap<BlockState> blockState2RuntimeId = new TObjectIntHashMap<>(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1);
    private final TIntIntMap legacyId2RuntimeId = new TIntIntHashMap(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1,-1);
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(RUNTIMEID_TABLE_CAPACITY);
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    public LevelPaletteManager() {
        InputStream stream = NukkitServer.class.getClassLoader().getResourceAsStream("runtimeid_table.json");
        if (stream == null) {
            throw new AssertionError("Static RuntimeID table not found");
        }

        CollectionType type = NukkitServer.JSON_MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, RuntimeEntry.class);
        ArrayList<RuntimeEntry> entries;
        try {
            entries = NukkitServer.JSON_MAPPER.readValue(stream, type);
        } catch (Exception e) {
            throw new AssertionError("Could not load RuntimeID table");
        }

        for (RuntimeEntry entry : entries) {
            BlockType blockType = BlockTypes.byId(entry.id);
            Metadata metadata = entry.data == 0 ? null : MetadataSerializers.deserializeMetadata(blockType, (short) entry.data);
            BlockState state = new NukkitBlockStateBuilder().setBlockType(blockType).setMetadata(metadata).build();
            registerRuntimeId(entry.runtimeId, state);
            registerLegacyId(entry.runtimeId, (entry.id << 4) | entry.data);
        }
    }

    public Optional<BlockState> getBlockState(int runtimeId) {
        r.lock();
        try {
            return Optional.ofNullable(runtimeId2BlockState.get(runtimeId));
        } finally {
            r.unlock();
        }
    }

    public int getOrCreateRuntimeId(BlockState state) {
        Preconditions.checkNotNull(state, "state");
        int runtimeId;
        r.lock();
        try {
            runtimeId = blockState2RuntimeId.get(state);
        } finally {
            r.unlock();
        }

        if (runtimeId == -1) {
            runtimeId = runtimeIdAllocator.getAndIncrement();
            registerRuntimeId(runtimeId, state);
        }
        return runtimeId;
    }

    public int fromLegacy(int legacyId, byte data) {
        int id;
        if ((id = legacyId2RuntimeId.get((legacyId << 4) | data)) == -1) {
            throw new IllegalArgumentException("Unknown legacy id");
        }
        return id;
    }

    private void registerRuntimeId(int runtimeId, BlockState state) {
        if (runtimeId2BlockState.containsKey(runtimeId)) {
            throw new IllegalArgumentException("RuntimeID already registered");
        }

        w.lock();
        try {
            runtimeId2BlockState.put(runtimeId, state);
            blockState2RuntimeId.put(state, runtimeId);
        } finally {
            w.unlock();
        }
    }

    private void registerLegacyId(int runtimeId, int legacyId) {
        if (legacyId2RuntimeId.containsKey(legacyId)) {
            throw new IllegalArgumentException("LegacyID already registered");
        }

        legacyId2RuntimeId.put(legacyId, runtimeId);
    }

    @AllArgsConstructor
    private static class RuntimeEntry {
        @JsonProperty("runtimeID")
        private final int runtimeId;
        private final String name;
        private final int id;
        private final int data;
    }
}
