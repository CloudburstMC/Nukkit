/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.level.chunk;

import cn.nukkit.api.block.Block;
import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockType;
import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.block.entity.BlockEntity;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.ChunkSnapshot;
import cn.nukkit.api.level.data.Biome;
import cn.nukkit.server.level.biome.NukkitBiome;
import cn.nukkit.server.metadata.MetadataSerializer;
import cn.nukkit.server.nbt.NBTEncodingType;
import cn.nukkit.server.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.server.nbt.stream.NBTOutputStream;
import cn.nukkit.server.nbt.stream.SwappedDataOutputStream;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.nbt.tag.IntTag;
import cn.nukkit.server.nbt.tag.Tag;
import cn.nukkit.server.network.minecraft.packet.FullChunkDataPacket;
import cn.nukkit.server.network.minecraft.packet.WrappedPacket;
import cn.nukkit.server.network.minecraft.wrapper.DefaultWrapperHandler;
import cn.nukkit.server.network.minecraft.wrapper.WrapperHandler;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

@Log4j2
public class SectionedChunk extends SectionedChunkSnapshot implements Chunk, FullChunkDataPacketCreator {
    private static final WrapperHandler compressionHandler = new DefaultWrapperHandler();
    private final Level level;
    private final TIntObjectMap<CompoundTag> serializedBlockEntities = new TIntObjectHashMap<>();
    private byte[] precompressed;

    public SectionedChunk(int x, int z, Level level) {
        this(new ChunkSection[16], x, z, level);
    }

    public SectionedChunk(ChunkSection[] sections, int x, int z, Level level) {
        super(sections, x, z);
        this.level = level;
    }

    public SectionedChunk(ChunkSection[] sections, int x, int z, Level level, byte[] biomeId, byte[] height) {
        super(sections, x, z, biomeId, height);
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    @Synchronized
    public Block getBlock(int x, int y, int z) {
        return (Block) super.getBlock(level, this, x, y, z);
    }

    @Override
    public Block setBlock(int x, int y, int z, BlockState state) {
        return setBlock(x, y, z, state, true);
    }

    @Override
    @Synchronized
    public Block setBlock(int x, int y, int z, BlockState state, boolean shouldRecalculateLight) {
        Preconditions.checkNotNull(state, "state");
        checkPosition(x, y, z);

        ChunkSection section = getOrCreateSection(y >> 4);
        section.setBlockId(x, y & 15, z, (byte) state.getBlockType().getId());
        section.setBlockData(x, y & 15, z, (byte) MetadataSerializer.serializeMetadata(state));

        if (shouldRecalculateLight) {
            // Recalculate the height map and lighting for this chunk section.
            if (height[(z << 4) + x] <= y && state.getBlockType() != BlockTypes.AIR) {
                // Slight optimization
                height[(z << 4) + x] = (byte) y;
            } else {
                height[(z << 4) + x] = (byte) calculateHighestLayer(x, z);
            }

            populateSkyLightAt(x, z);
            calculateBlockLight(x, y, z);
        }

        // now set the block entity, if any
        int pos = xyzIdx(x, y, z);
        Optional<BlockEntity> entity = state.getBlockEntity();
        if (entity.isPresent()) {
            CompoundTag blockEntityTag = MetadataSerializer.serializeNBT(state);
            Map<String, Tag<?>> beModifiedMap = new HashMap<>(blockEntityTag.getValue());
            beModifiedMap.put("x", new IntTag("x", x + (this.x << 4)));
            beModifiedMap.put("y", new IntTag("y", y));
            beModifiedMap.put("z", new IntTag("z", z + (this.z << 4)));
            serializedBlockEntities.put(pos, new CompoundTag("", beModifiedMap));
            blockEntities.put(pos, entity.get());
        } else {
            serializedBlockEntities.remove(pos);
            blockEntities.remove(pos);
        }

        precompressed = null;
        return getBlock(x, y, z);
    }

    @Override
    @Synchronized
    public void setBiome(int x, int z, Biome id) {
        checkPosition(x, z);
        Preconditions.checkNotNull(id, "id");
        biomeId[(z << 4) | x] = NukkitBiome.idFromApi(id);
    }

    @Synchronized
    public ChunkSection getOrCreateSection(int y) {
        if (y >= sections.length) {
            throw new IllegalArgumentException("expected y to be up to " + (sections.length - 1) + ", got " + y);
        }
        ChunkSection section = sections[y];
        if (section == null) {
            sections[y] = section = new ChunkSection();
        }
        return section;
    }

    @Synchronized
    public Optional<ChunkSection> getSection(int y) {
        if (y >= sections.length) {
            throw new IllegalArgumentException("expected y to be up to " + (sections.length - 1) + ", got " + y);
        }
        return Optional.ofNullable(sections[y]);
    }

    private int calculateHighestLayer(int x, int z) {
        for (int i = sections.length - 1; i >= 0; i--) {
            ChunkSection section = sections[i];
            if (section != null) {
                for (int j = 15; j >= 0; j--) {
                    if (section.getBlockId(x, j, z) != 0) {
                        return j + (i << 4);
                    }
                }
            }
        }
        return 0;
    }

    private void populateSkyLightAt(int x, int z) {
        int maxHeight = height[(z << 4) + x];

        for (int y = 255; y > maxHeight; y--) {
            ChunkSection section = sections[y >> 4];
            if (section != null) {
                section.setSkyLight(x, y & 15, z, (byte) 15);
            }
        }

        // From the top, however...
        boolean blocked = false;
        for (int y = maxHeight; y > 0; y--) {
            BlockType type = BlockTypes.byId(sections[y >> 4].getBlockId(x, y & 15, z));
            byte light = 15;
            if (!blocked) {
                if (!type.isTransparent()) {
                    blocked = true;
                    light = 0;
                }
            } else {
                light = 0;
            }

            ChunkSection section = sections[y >> 4];
            if (section != null) {
                section.setSkyLight(x, y & 15, z, light);
            }
        }
    }

    @Override
    @Synchronized
    public ChunkSnapshot toSnapshot() {
        ChunkSection[] sections = this.sections.clone();
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] != null) {
                sections[i] = sections[i].copy();
            }
        }
        SectionedChunkSnapshot snapshot = new SectionedChunkSnapshot(sections, x, z);
        System.arraycopy(biomeId, 0, snapshot.biomeId, 0, biomeId.length);
        System.arraycopy(height, 0, snapshot.height, 0, height.length);
        snapshot.blockEntities.putAll(blockEntities);
        return snapshot;
    }

    @Synchronized
    public void recalculateLight() {
        recalculateHeightMap();
        populateSkyLight();
        precompressed = null;
    }

    @Synchronized
    public void recalculateHeightMap() {
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                int highest = calculateHighestLayer(x, z);
                height[(z << 4) + x] = (byte) highest;
            }
        }
    }

    private void populateSkyLight() {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                populateSkyLightAt(x, z);
            }
        }
    }

    private void calculateBlockLight(int x, int y, int z) {
        Queue<Vector3i> spread = new ArrayDeque<>();
        Queue<LightRemoveData> remove = new ArrayDeque<>();
        TLongSet visitedSpread = new TLongHashSet();
        TLongSet visitedRemove = new TLongHashSet();

        ChunkSection section = getOrCreateSection(y >> 4);
        BlockType ourType = BlockTypes.byId(section.getBlockId(x, y & 15, z));
        byte currentBlockLight = section.getBlockLight(x, y & 15, z);
        byte newBlockLight = (byte) ourType.emitsLight();

        if (currentBlockLight != newBlockLight) {
            section.setBlockLight(x, y & 15, z, newBlockLight);

            if (newBlockLight < currentBlockLight) {
                remove.add(new LightRemoveData(new Vector3i(x, y, z), currentBlockLight));
                visitedRemove.add(xyzIdx(x, y, z));
            } else {
                spread.add(new Vector3i(x, y, z));
                visitedSpread.add(xyzIdx(x, y, z));
            }
        }

        LightRemoveData toRemove;
        while ((toRemove = remove.poll()) != null) {
            computeRemoveBlockLight(toRemove.data.sub(1, 0, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(1, 0, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.sub(0, 1, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(0, 1, 0), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.sub(0, 0, 1), toRemove.light, remove, spread, visitedRemove, visitedSpread);
            computeRemoveBlockLight(toRemove.data.add(0, 0, 1), toRemove.light, remove, spread, visitedRemove, visitedSpread);
        }

        Vector3i toSpread;
        while ((toSpread = spread.poll()) != null) {
            ChunkSection cs = getOrCreateSection(toSpread.getY() >> 4);
            byte adjustedLight = (byte) (cs.getBlockLight(toSpread.getX(), toSpread.getY() & 15, toSpread.getZ())
                    - BlockTypes.byId(cs.getBlockId(toSpread.getX(), toSpread.getY() & 15, toSpread.getZ())).filtersLight());

            if (adjustedLight >= 1) {
                computeSpreadBlockLight(toSpread.sub(1, 0, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(1, 0, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.sub(0, 1, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(0, 1, 0), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.sub(0, 0, 1), adjustedLight, spread, visitedSpread);
                computeSpreadBlockLight(toSpread.add(0, 0, 1), adjustedLight, spread, visitedSpread);
            }
        }
    }

    private void computeRemoveBlockLight(Vector3i loc, byte currentLight, Queue<LightRemoveData> removalQueue, Queue<Vector3i> spreadQueue, TLongSet removalVisited, TLongSet spreadVisited) {
        if (loc.getY() >= 256) {
            return;
        }
        ChunkSection section = getOrCreateSection(loc.getY() >> 4);
        byte presentLight = section.getBlockLight(loc.getX(), loc.getY() & 15, loc.getZ());
        long idx = xyzIdx(loc.getX(), loc.getY(), loc.getZ());
        if (presentLight != 0 && presentLight < currentLight) {
            section.setBlockLight(loc.getX(), loc.getY() & 15, loc.getZ(), (byte) 0);
            if (removalVisited.add(idx)) {
                if (presentLight > 1) {
                    removalQueue.add(new LightRemoveData(loc, presentLight));
                }
            }
        } else if (presentLight >= currentLight) {
            if (spreadVisited.add(idx)) {
                spreadQueue.add(loc);
            }
        }
    }

    private void computeSpreadBlockLight(Vector3i loc, byte currentLight, Queue<Vector3i> spreadQueue, TLongSet spreadVisited) {
        if (loc.getY() >= 256) {
            return;
        }
        ChunkSection section = getOrCreateSection(loc.getY() >> 4);
        byte presentLight = section.getBlockLight(loc.getX(), loc.getY() & 15, loc.getZ());
        long idx = xyzIdx(loc.getX(), loc.getY(), loc.getZ());
        if (presentLight < currentLight) {
            section.setBlockLight(loc.getX(), loc.getY() & 15, loc.getZ(), currentLight);
            if (spreadVisited.add(idx)) {
                if (presentLight > 1) {
                    spreadQueue.add(loc);
                }
            }
        }
    }

    @Override
    @Synchronized
    public WrappedPacket createFullChunkDataPacket() {
        if (precompressed != null) {
            ByteBuf payload = PooledByteBufAllocator.DEFAULT.directBuffer(precompressed.length);
            payload.writeBytes(precompressed);
            WrappedPacket packet = new WrappedPacket();
            packet.setPayload(payload);
            return packet;
        }

        FullChunkDataPacket packet = new FullChunkDataPacket();
        packet.setChunkX(x);
        packet.setChunkZ(z);

        // Block Entities
        int nbtSize = 0;
        CanWriteToBB blockEntitiesStream = null;
        if (!serializedBlockEntities.isEmpty()) {
            blockEntitiesStream = new CanWriteToBB();
            try (NBTOutputStream writer = new NBTOutputStream(new SwappedDataOutputStream(blockEntitiesStream), NBTEncodingType.BEDROCK)) {
                for (CompoundTag blockEntity : serializedBlockEntities.valueCollection()) {
                    writer.write(blockEntity);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to write block entity NBT");
            }
            nbtSize = blockEntitiesStream.size();
        }

        // We don't have to send empty chunks
        int topBlank = 0;
        for (int i = sections.length - 1; i >= 0; i--) {
            ChunkSection section = sections[i];
            if (section == null || section.isEmpty()) {
                topBlank = i + 1;
            } else {
                break;
            }
        }

        // Chunk sections
        int bufferSize = 1 + 6145 * topBlank + 768 + 2 + nbtSize;
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        buffer.put((byte) topBlank);

        for (int i = 0; i < topBlank; i++) {
            ChunkSection section = sections[i];
            buffer.put((byte) 0); // Chunk section version
            if (section != null) {
                buffer.put(section.getIds());
                buffer.put(section.getData().getData());
            } else {
                buffer.position(buffer.position() + 6144);
            }
        }

        // Heightmap
        buffer.put(height);
        // Biomes
        buffer.put(biomeId);

        // Extra data TODO: Implement
        buffer.putShort((short) 0);

        if (blockEntitiesStream != null) {
            blockEntitiesStream.writeTo(buffer);
        }

        packet.setData(buffer.array());

        WrappedPacket wrappedPacket = new WrappedPacket();
        wrappedPacket.setPayload(compressionHandler.compressPackets(Collections.singletonList(packet)));

        precompressed = new byte[wrappedPacket.getPayload().readableBytes()];
        wrappedPacket.getPayload().readBytes(precompressed);
        return wrappedPacket;
    }

    private static class CanWriteToBB extends FastByteArrayOutputStream {
        public CanWriteToBB() {
            super(8192);
        }

        public void writeTo(ByteBuffer byteBuffer) {
            byteBuffer.put(array, 0, position);
        }
    }

    private static class LightRemoveData {
        private final Vector3i data;
        private final byte light;

        private LightRemoveData(Vector3i data, byte light) {
            this.data = data;
            this.light = light;
        }
    }
}
