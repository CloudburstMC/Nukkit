package com.nukkitx.server.level.chunk;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.chunk.ChunkSnapshot;
import com.nukkitx.api.level.data.Biome;
import com.nukkitx.api.metadata.blockentity.BlockEntity;
import com.nukkitx.nbt.NBTEncodingType;
import com.nukkitx.nbt.stream.FastByteArrayOutputStream;
import com.nukkitx.nbt.stream.LittleEndianDataOutputStream;
import com.nukkitx.nbt.stream.NBTOutputStream;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.IntTag;
import com.nukkitx.nbt.tag.Tag;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.level.biome.NukkitBiome;
import com.nukkitx.server.metadata.MetadataSerializers;
import com.nukkitx.server.network.bedrock.BedrockPacketCodec;
import com.nukkitx.server.network.bedrock.packet.FullChunkDataPacket;
import com.nukkitx.server.network.bedrock.packet.WrappedPacket;
import com.nukkitx.server.network.bedrock.wrapper.DefaultWrapperHandler;
import com.nukkitx.server.network.util.VarInts;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.*;

@Log4j2
public class SectionedChunk extends SectionedChunkSnapshot implements Chunk, FullChunkDataPacketCreator {
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
        section.setBlockId(x, y & 15, z, 0, NukkitLevel.getPaletteManager().getOrCreateRuntimeId(state));

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
            CompoundTag blockEntityTag = MetadataSerializers.serializeNBT(state);
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
                    if (section.getBlockId(x, j, z, 0) != 0) {
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
            BlockType type = BlockTypes.byId(sections[y >> 4].getBlockId(x, y & 15, z, 0));
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
        BlockType ourType = BlockTypes.byId(section.getBlockId(x, y & 15, z, 0));
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
                    - BlockTypes.byId(cs.getBlockId(toSpread.getX(), toSpread.getY() & 15, toSpread.getZ(), 0)).filtersLight());

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

        // Block Entities
        int nbtSize = 0;
        CanWriteToBB blockEntitiesStream = null;
        if (!serializedBlockEntities.isEmpty()) {
            blockEntitiesStream = new CanWriteToBB();
            try (NBTOutputStream writer = new NBTOutputStream(new LittleEndianDataOutputStream(blockEntitiesStream), NBTEncodingType.BEDROCK)) {
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
        int bufferSize = 1 + 4096 * topBlank + 768 + 2 + nbtSize;
        ByteBuf byteBuf = Unpooled.buffer(bufferSize);
        byteBuf.markReaderIndex();
        byteBuf.writeByte((byte) topBlank);

        for (int i = 0; i < topBlank; i++) {
            getOrCreateSection(i).writeTo(byteBuf);
        }

        // Heightmap
        byteBuf.writeBytes(height);
        // Biomes
        byteBuf.writeBytes(biomeId);

        // Extra data TODO: Implement
        VarInts.writeInt(byteBuf, 0);
        VarInts.writeInt(byteBuf, 0);

        if (blockEntitiesStream != null) {
            blockEntitiesStream.writeTo(byteBuf);
        }

        FullChunkDataPacket packet = new FullChunkDataPacket();
        packet.setChunkX(x);
        packet.setChunkZ(z);
        byte[] chunkData = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(chunkData);
        packet.setData(chunkData);

        WrappedPacket wrappedPacket = new WrappedPacket();
        wrappedPacket.setPayload(DefaultWrapperHandler.HIGH_COMPRESSION.compressPackets(BedrockPacketCodec.DEFAULT, packet));

        precompressed = new byte[wrappedPacket.getPayload().readableBytes()];
        wrappedPacket.getPayload().readBytes(precompressed);
        return wrappedPacket;
    }

    private static class CanWriteToBB extends FastByteArrayOutputStream {
        public CanWriteToBB() {
            super(8192);
        }

        public void writeTo(ByteBuf buf) {
            buf.writeBytes(array, 0, position);
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
