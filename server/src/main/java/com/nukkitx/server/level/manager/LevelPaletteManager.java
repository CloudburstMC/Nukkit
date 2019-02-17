package com.nukkitx.server.level.manager;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.base.Preconditions;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.network.VarInts;
import com.nukkitx.protocol.bedrock.v332.BedrockUtils;
import com.nukkitx.server.NukkitServer;
import com.nukkitx.server.block.NukkitBlockStateBuilder;
import com.nukkitx.server.metadata.MetadataSerializers;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LevelPaletteManager {
    private static final int RUNTIMEID_TABLE_CAPACITY = 4467;
    private final ArrayList<BlockState> runtimeId2BlockState;
    private final TObjectIntMap<BlockState> blockState2RuntimeId = new TObjectIntHashMap<>(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1);
    private final TIntIntMap legacyId2RuntimeId = new TIntIntHashMap(RUNTIMEID_TABLE_CAPACITY, 0.5f, -1, -1);
    private final ByteBuf cachedPallete;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private int runtimeIdAllocator = 0;
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

        cachedPallete = Unpooled.buffer();
        VarInts.writeUnsignedInt(cachedPallete, entries.size());

        runtimeId2BlockState = new ArrayList<>(entries.size());

        for (RuntimeEntry entry : entries) {
            try {
                BlockType blockType = BlockTypes.byId(entry.id > 255 ? 255 - entry.id : entry.id);
                Metadata metadata = entry.data == 0 ? null : MetadataSerializers.deserializeMetadata(blockType, (short) entry.data);
                BlockState state = new NukkitBlockStateBuilder().setBlockType(blockType).setMetadata(metadata).build();
                registerRuntimeId(state, (entry.id << 4) | entry.data);
            } catch (IllegalArgumentException e) {
                // ignore
            }

            BedrockUtils.writeString(cachedPallete, entry.name);
            cachedPallete.writeShortLE(entry.data);
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
            int id = state.getBlockType().getId();
            short meta = MetadataSerializers.serializeMetadata(state);
            runtimeId = registerRuntimeId(state, (id << 4) | meta);
        }
        return runtimeId;
    }

    public int fromLegacy(int id, byte data) {
        int runtimeId;
        if ((runtimeId = legacyId2RuntimeId.get((id << 4) | data)) == -1) {
            throw new IllegalArgumentException("Unknown legacy id");
        }
        return runtimeId;
    }

    private int registerRuntimeId(BlockState state, int legacyId) {
        if (legacyId2RuntimeId.containsKey(legacyId)) {
            throw new IllegalArgumentException("LegacyID already registered");
        }

        int runtimeId;

        w.lock();
        try {
            runtimeId = runtimeIdAllocator++;
            runtimeId2BlockState.add(state);
            blockState2RuntimeId.put(state, runtimeId);
            legacyId2RuntimeId.put(legacyId, runtimeId);
        } finally {
            w.unlock();
        }
        return runtimeId;
    }

    public ByteBuf getCachedPallete() {
        return cachedPallete.retainedSlice();
    }

    @AllArgsConstructor
    private static class RuntimeEntry {
        private final String name;
        private final int id;
        private final int data;
    }
}
