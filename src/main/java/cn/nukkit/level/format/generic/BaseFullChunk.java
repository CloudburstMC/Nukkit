package cn.nukkit.level.format.generic;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.network.protocol.BatchPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
/*

 */
public abstract class BaseFullChunk implements FullChunk, ChunkManager {


    protected Map<Long, Entity> entities;

    protected Map<Long, BlockEntity> tiles;

    protected Map<Integer, BlockEntity> tileList;

    /**
     * encoded as:
     * <p>
     * (x &lt;&lt; 4) | z
     */
    protected byte[] biomes;

    protected byte[] blocks;

    protected byte[] data;

    protected byte[] skyLight;

    protected byte[] blockLight;

    protected byte[] heightMap;

    protected List<CompoundTag> NBTtiles;

    protected List<CompoundTag> NBTentities;

    protected Map<Integer, Integer> extraData;

    protected LevelProvider provider;

    protected Class<? extends LevelProvider> providerClass;

    protected long changes;

    protected boolean isInit;

    protected BatchPacket chunkPacket;

    protected int x;

    protected int z;

    protected long hash;

    public void setNBTtiles(List<CompoundTag> NBTtiles) {
        this.NBTtiles = NBTtiles;
    }

    public void setNBTentities(List<CompoundTag> NBTentities) {
        this.NBTentities = NBTentities;
    }

    public List<CompoundTag> getNBTtiles() {
        return NBTtiles;
    }

    public List<CompoundTag> getNBTentities() {
        return NBTentities;
    }

    @Override
    public BaseFullChunk clone() {
        final BaseFullChunk chunk;
        try {
            chunk = (BaseFullChunk) super.clone();
        } catch (final CloneNotSupportedException e) {
            return null;
        }
        if (this.biomes != null) {
            chunk.biomes = this.biomes.clone();
        }

        if (this.blocks != null) {
            chunk.blocks = this.blocks.clone();
        }

        if (this.data != null) {
            chunk.data = this.data.clone();
        }

        if (this.skyLight != null) {
            chunk.skyLight = this.skyLight.clone();
        }

        if (this.blockLight != null) {
            chunk.blockLight = this.blockLight.clone();
        }

        if (this.heightMap != null) {
            chunk.heightMap = this.getHeightMapArray().clone();
        }
        return chunk;
    }

    public BatchPacket getChunkPacket() {
        final BatchPacket pk = this.chunkPacket;
        if (pk != null) {
            pk.trim();
        }
        return this.chunkPacket;
    }

    public void setChunkPacket(final BatchPacket packet) {
        if (packet != null) {
            packet.trim();
        }
        this.chunkPacket = packet;
    }

    @Override
    public final int getX() {
        return this.x;
    }

    @Override
    public final void setX(final int x) {
        this.x = x;
        this.hash = Level.chunkHash(x, this.getZ());
    }

    @Override
    public final int getZ() {
        return this.z;
    }

    @Override
    public final void setZ(final int z) {
        this.z = z;
        this.hash = Level.chunkHash(this.getX(), z);
    }

    @Override
    public void setPosition(final int x, final int z) {
        this.x = x;
        this.z = z;
        this.hash = Level.chunkHash(x, z);
    }

    @Override
    public final long getIndex() {
        return this.hash;
    }

    @Override
    public LevelProvider getProvider() {
        return this.provider;
    }

    @Override
    public void setProvider(final LevelProvider provider) {
        this.provider = provider;
    }

    @Override
    public int getBlockExtraData(final int x, final int y, final int z) {
        final int index = Level.chunkBlockHash(x, y, z);
        if (this.extraData != null && this.extraData.containsKey(index)) {
            return this.extraData.get(index);
        }

        return 0;
    }

    @Override
    public void setBlockExtraData(final int x, final int y, final int z, final int data) {
        if (data == 0) {
            if (this.extraData != null) {
                this.extraData.remove(Level.chunkBlockHash(x, y, z));
            }
        } else {
            if (this.extraData == null) {
                this.extraData = new Int2ObjectOpenHashMap<>();
            }
            this.extraData.put(Level.chunkBlockHash(x, y, z), data);
        }

        this.setChanged(true);
    }

    @Override
    public int getHighestBlockAt(final int x, final int z) {
        return this.getHighestBlockAt(x, z, true);
    }

    @Override
    public int getHighestBlockAt(final int x, final int z, final boolean cache) {
        if (cache) {
            final int h = this.getHeightMap(x, z);
            if (h != 0 && h != 255) {
                return h;
            }
        }
        for (int y = 255; y >= 0; --y) {
            if (this.getBlockId(x, y, z) != 0x00) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return 0;
    }

    @Override
    public int getHeightMap(final int x, final int z) {
        return this.heightMap[z << 4 | x] & 0xFF;
    }

    @Override
    public void setHeightMap(final int x, final int z, final int value) {
        this.heightMap[z << 4 | x] = (byte) value;
    }

    @Override
    public void recalculateHeightMap() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                this.setHeightMap(x, z, this.getHighestBlockAt(x, z, false));
            }
        }
    }

    @Override
    public void populateSkyLight() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                final int top = this.getHeightMap(x, z);
                for (int y = 255; y > top; --y) {
                    this.setBlockSkyLight(x, y, z, 15);
                }
                for (int y = top; y >= 0; --y) {
                    if (Block.solid[this.getBlockId(x, y, z)]) {
                        break;
                    }
                    this.setBlockSkyLight(x, y, z, 15);
                }
                this.setHeightMap(x, z, this.getHighestBlockAt(x, z, false));
            }
        }
    }

    @Override
    public int getBiomeId(final int x, final int z) {
        return this.biomes[x << 4 | z] & 0xFF;
    }

    @Override
    public void setBiomeId(final int x, final int z, final byte biomeId) {
        this.setChanged();
        this.biomes[x << 4 | z] = biomeId;
    }

    @Override
    public boolean isLightPopulated() {
        return true;
    }

    @Override
    public void setLightPopulated(final boolean value) {

    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public void addEntity(final Entity entity) {
        if (this.entities == null) {
            this.entities = new Long2ObjectOpenHashMap<>();
        }
        this.entities.put(entity.getId(), entity);
        if (!(entity instanceof Player) && this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeEntity(final Entity entity) {
        if (this.entities != null) {
            this.entities.remove(entity.getId());
            if (!(entity instanceof Player) && this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public void addBlockEntity(final BlockEntity blockEntity) {
        if (this.tiles == null) {
            this.tiles = new Long2ObjectOpenHashMap<>();
            this.tileList = new Int2ObjectOpenHashMap<>();
        }
        this.tiles.put(blockEntity.getId(), blockEntity);
        final int index = (blockEntity.getFloorZ() & 0x0f) << 12 | (blockEntity.getFloorX() & 0x0f) << 8 | blockEntity.getFloorY() & 0xff;
        if (this.tileList.containsKey(index) && !this.tileList.get(index).equals(blockEntity)) {
            final BlockEntity entity = this.tileList.get(index);
            this.tiles.remove(entity.getId());
            entity.close();
        }
        this.tileList.put(index, blockEntity);
        if (this.isInit) {
            this.setChanged();
        }
    }

    @Override
    public void removeBlockEntity(final BlockEntity blockEntity) {
        if (this.tiles != null) {
            this.tiles.remove(blockEntity.getId());
            final int index = (blockEntity.getFloorZ() & 0x0f) << 12 | (blockEntity.getFloorX() & 0x0f) << 8 | blockEntity.getFloorY() & 0xff;
            this.tileList.remove(index);
            if (this.isInit) {
                this.setChanged();
            }
        }
    }

    @Override
    public Map<Long, Entity> getEntities() {
        return this.entities == null ? Collections.emptyMap() : this.entities;
    }

    @Override
    public Map<Long, BlockEntity> getBlockEntities() {
        return this.tiles == null ? Collections.emptyMap() : this.tiles;
    }

    @Override
    public BlockEntity getTile(final int x, final int y, final int z) {
        return this.tileList != null ? this.tileList.get(z << 12 | x << 8 | y) : null;
    }

    @Override
    public boolean isLoaded() {
        return this.getProvider() != null && this.getProvider().isChunkLoaded(this.getX(), this.getZ());
    }

    @Override
    public boolean load() throws IOException {
        return this.load(true);
    }

    @Override
    public boolean load(final boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public boolean unload() throws Exception {
        return this.unload(true, true);
    }

    @Override
    public boolean unload(final boolean save) {
        return this.unload(save, true);
    }

    @Override
    public boolean unload(final boolean save, final boolean safe) {
        final LevelProvider provider = this.getProvider();
        if (provider == null) {
            return true;
        }
        if (save && this.changes != 0) {
            provider.saveChunk(this.getX(), this.getZ());
        }
        if (safe) {
            for (final Entity entity : this.getEntities().values()) {
                if (entity instanceof Player) {
                    return false;
                }
            }
        }
        for (final Entity entity : new ArrayList<>(this.getEntities().values())) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        for (final BlockEntity blockEntity : new ArrayList<>(this.getBlockEntities().values())) {
            blockEntity.close();
        }
        this.provider = null;
        return true;
    }

    @Override
    public void initChunk() {
        if (this.getProvider() != null && !this.isInit) {
            boolean changed = false;
            if (this.NBTentities != null) {
                this.getProvider().getLevel().timings.syncChunkLoadEntitiesTimer.startTiming();
                for (final CompoundTag nbt : this.NBTentities) {
                    if (!nbt.contains("id")) {
                        this.setChanged();
                        continue;
                    }
                    final ListTag pos = nbt.getList("Pos");
                    if (((NumberTag) pos.get(0)).getData().intValue() >> 4 != this.getX() || ((NumberTag) pos.get(2)).getData().intValue() >> 4 != this.getZ()) {
                        changed = true;
                        continue;
                    }
                    final Entity entity = Entity.createEntity(nbt.getString("id"), this, nbt);
                    addEntity(entity);
                    if (entity != null) {
                        changed = true;
                    }
                }
                this.getProvider().getLevel().timings.syncChunkLoadEntitiesTimer.stopTiming();
                this.NBTentities = null;
            }

            if (this.NBTtiles != null) {
                this.getProvider().getLevel().timings.syncChunkLoadBlockEntitiesTimer.startTiming();
                for (final CompoundTag nbt : this.NBTtiles) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true;
                            continue;
                        }
                        if (nbt.getInt("x") >> 4 != this.getX() || nbt.getInt("z") >> 4 != this.getZ()) {
                            changed = true;
                            continue;
                        }
                        final BlockEntity blockEntity = BlockEntity.createBlockEntity(nbt.getString("id"), this, nbt);
                        addBlockEntity(blockEntity);
                        if (blockEntity == null) {
                            changed = true;
                        }
                    }
                }
                this.getProvider().getLevel().timings.syncChunkLoadBlockEntitiesTimer.stopTiming();
                this.NBTtiles = null;
            }

            this.setChanged(changed);

            this.isInit = true;
        }
    }

    @Override
    public byte[] getBiomeIdArray() {
        return this.biomes;
    }

    @Override
    public byte[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public byte[] getBlockIdArray() {
        return this.blocks;
    }

    @Override
    public byte[] getBlockDataArray() {
        return this.data;
    }

    @Override
    public Map<Integer, Integer> getBlockExtraDataArray() {
        return this.extraData == null ? Collections.emptyMap() : this.extraData;
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        return this.skyLight;
    }

    @Override
    public byte[] getBlockLightArray() {
        return this.blockLight;
    }

    @Override
    public byte[] toFastBinary() {
        return this.toBinary();
    }

    @Override
    public boolean hasChanged() {
        return this.changes != 0;
    }

    @Override
    public void setChanged() {
        this.changes++;
        this.chunkPacket = null;
    }

    @Override
    public void setChanged(final boolean changed) {
        if (changed) {
            this.setChanged();
        } else {
            this.changes = 0;
        }
    }

    public long getChanges() {
        return this.changes;
    }

    @Override
    public int getBlockIdAt(final int x, final int y, final int z) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            return this.getBlockId(x & 15, y, z & 15);
        }
        return 0;
    }

    @Override
    public void setBlockFullIdAt(final int x, final int y, final int z, final int fullId) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            this.setFullBlockId(x & 15, y, z & 15, fullId);
        }
    }

    @Override
    public void setBlockIdAt(final int x, final int y, final int z, final int id) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            this.setBlockId(x & 15, y, z & 15, id);
        }
    }

    @Override
    public void setBlockAt(final int x, final int y, final int z, final int id, final int data) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            this.setBlock(x & 15, y, z & 15, id, data);
        }
    }

    @Override
    public int getBlockDataAt(final int x, final int y, final int z) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            return this.getBlockIdAt(x & 15, y, z & 15);
        }
        return 0;
    }

    @Override
    public void setBlockDataAt(final int x, final int y, final int z, final int data) {
        if (x >> 4 == this.getX() && z >> 4 == this.getZ()) {
            this.setBlockData(x & 15, y, z & 15, data);
        }
    }

    @Override
    public BaseFullChunk getChunk(final int chunkX, final int chunkZ) {
        if (chunkX == this.getX() && chunkZ == this.getZ()) {
            return this;
        }
        return null;
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ) {
        this.setChunk(chunkX, chunkZ, null);
    }

    @Override
    public void setChunk(final int chunkX, final int chunkZ, final BaseFullChunk chunk) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSeed() {
        throw new UnsupportedOperationException("WoolChunk does not have a seed");
    }

    public boolean compress() {
        final BatchPacket pk = this.chunkPacket;
        if (pk != null) {
            pk.trim();
            return true;
        }
        return false;
    }

}
