package cn.nukkit.server.level.manager;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockType;
import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.server.NukkitServer;
import cn.nukkit.server.block.NukkitBlockStateBuilder;
import cn.nukkit.server.metadata.MetadataSerializers;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TObjectIntMap;
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
    private final TIntObjectMap<BlockState> runtimeId2BlockState = new TIntObjectHashMap<>(4467, 0.5f, -1);
    private final TObjectIntMap<BlockState> blockState2RuntimeId = new TObjectIntHashMap<>(4467, 0.5f, -1);
    private final AtomicInteger runtimeIdAllocator = new AtomicInteger(4467);
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

    @AllArgsConstructor
    private static class RuntimeEntry {
        @JsonProperty("runtimeID")
        private final int runtimeId;
        private final String name;
        private final int id;
        private final int data;
    }
}
