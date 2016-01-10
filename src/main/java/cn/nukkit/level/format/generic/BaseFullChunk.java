package cn.nukkit.level.format.generic;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.NumberTag;
import cn.nukkit.tile.Tile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class BaseFullChunk implements FullChunk {
    protected Map<Long, Entity> entities = new HashMap<>();

    protected Map<Long, Tile> tiles = new HashMap<>();

    protected Map<Integer, Tile> tileList = new HashMap<>();

    protected int[] biomeColors;

    protected byte[] blocks;

    protected byte[] data;

    protected byte[] skyLight;

    protected byte[] blockLight;

    protected int[] heightMap;

    protected List<CompoundTag> NBTtiles;

    protected List<CompoundTag> NBTentities;

    protected Map<Integer, Integer> extraData = new HashMap<>();

    protected LevelProvider provider;

    protected int x;
    protected int z;

    protected boolean hasChanged = false;

    protected boolean isInit = false;

    @Override
    public BaseFullChunk clone() {
        BaseFullChunk chunk;
        try {
            chunk = (BaseFullChunk) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
        if (this.biomeColors != null) {
            chunk.biomeColors = this.getBiomeColorArray().clone();
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

    protected void checkOldBiomes(byte[] data) {
        if (data.length != 256) {
            return;
        }
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = Biome.getBiome(data[(z << 4) + x] & 0xff);
                this.setBiomeId(x, z, biome.getId());
                int c = biome.getColor();
                this.setBiomeColor(x, z, c >> 16, (c >> 8) & 0xff, c & 0xff);
            }
        }
    }

    public void initChunk() {
        if (this.getProvider() != null && !this.isInit) {
            boolean changed = false;
            if (this.NBTentities != null) {
                for (CompoundTag nbt : NBTentities) {
                    if (!nbt.contains("id")) {
                        this.setChanged();
                        continue;
                    }
                    ListTag pos = nbt.getList("Pos");
                    if ((((NumberTag) pos.get(0)).getData().intValue() >> 4) != this.x || ((((NumberTag) pos.get(2)).getData().intValue() >> 4) != this.z)) {
                        changed = true;
                        continue;
                    }
                    Entity entity = Entity.createEntity(nbt.getString("id"), this, nbt);
                    if (entity != null) {
                        entity.spawnToAll();
                    } else {
                        changed = true;
                        continue;
                    }
                }

                for (CompoundTag nbt : NBTtiles) {
                    if (nbt != null) {
                        if (!nbt.contains("id")) {
                            changed = true;
                            continue;
                        }
                        if ((nbt.getInt("x") >> 4) != this.x || ((nbt.getInt("z") >> 4) != this.z)) {
                            changed = true;
                            continue;
                        }
                        Tile tile = Tile.createTile(nbt.getString("id"), this, nbt);
                        if (tile == null) {
                            changed = true;
                            continue;
                        }
                    }
                }

                this.NBTentities = null;
                this.NBTtiles = null;
            }

            this.setChanged(changed);

            this.isInit = true;
        }
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getZ() {
        return z;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public LevelProvider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(LevelProvider provider) {
        this.provider = provider;
    }

    @Override
    public int getBiomeId(int x, int z) {
        return (this.getBiomeColorArray()[(z << 4) + x] & 0xFF000000) >> 24;
    }

    @Override
    public void setBiomeId(int x, int z, int biomeId) {
        this.hasChanged = true;
        this.getBiomeColorArray()[(z << 4) + x] = (this.getBiomeColorArray()[(z << 4) + x] & 0xFFFFFF) | (biomeId << 24);
    }

    @Override
    public int[] getBiomeColor(int x, int z) {
        int color = this.biomeColors[(z << 4) | x] & 0xFFFFFF;
        return new int[]{color >> 16, (color >> 8) & 0xFF, color & 0xFF};
    }

    @Override
    public void setBiomeColor(int x, int z, int R, int G, int B) {
        this.hasChanged = true;
        this.getBiomeColorArray()[(z << 4) + x] = (this.getBiomeColorArray()[(z << 4) + x] & 0xFF000000) | ((R & 0xFF) << 16) | ((G & 0xFF) << 8) | (B & 0XFF);
    }

    @Override
    public int getHeightMap(int x, int z) {
        return this.heightMap[(z << 4) + x];
    }

    @Override
    public void setHeightMap(int x, int z, int value) {
        this.heightMap[(z << 4) + x] = value;
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
    public int getBlockExtraData(int x, int y, int z) {
        int index = Level.chunkBlockHash(x, y, z);
        if (this.extraData.containsKey(index)) {
            return this.extraData.get(index);
        }

        return 0;
    }

    @Override
    public void setBlockExtraData(int x, int y, int z, int data) {
        if (data == 0) {
            this.extraData.remove(Level.chunkBlockHash(x, y, z));
        } else {
            this.extraData.put(Level.chunkBlockHash(x, y, z), data);
        }

        this.setChanged(true);
    }

    @Override
    public void populateSkyLight() {
        for (int z = 0; z < 16; ++z) {
            for (int x = 0; x < 16; ++x) {
                int top = this.getHeightMap(x, z);
                for (int y = 127; y > top; --y) {
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
    public int getHighestBlockAt(int x, int z) {
        return this.getHighestBlockAt(x, z, true);
    }

    @Override
    public int getHighestBlockAt(int x, int z, boolean cache) {
        if (cache) {
            int h = this.getHeightMap(x, z);
            if (h != 0 && h != 127) {
                return h;
            }
        }
        byte[] column = this.getBlockIdColumn(x, z);
        for (int y = 127; y >= 0; --y) {
            if (column[y] != 0x00) {
                this.setHeightMap(x, z, y);
                return y;
            }
        }
        return 0;
    }

    @Override
    public void addEntity(Entity entity) {
        this.entities.put(entity.getId(), entity);
        if (!(entity instanceof Player) && this.isInit) {
            this.hasChanged = true;
        }
    }

    @Override
    public void removeEntity(Entity entity) {
        this.entities.remove(entity.getId());
        if (!(entity instanceof Player) && this.isInit) {
            this.hasChanged = true;
        }
    }

    @Override
    public void addTile(Tile tile) {
        this.tiles.put(tile.getId(), tile);
        int index = (((int) tile.z & 0x0f) << 12) | (((int) tile.x & 0x0f) << 8) | ((int) tile.y & 0xff);
        if (this.tileList.containsKey(index) && !this.tileList.get(index).equals(tile)) {
            this.tileList.get(index).close();
        }
        this.tileList.put(index, tile);
        if (this.isInit) {
            this.hasChanged = true;
        }
    }

    @Override
    public void removeTile(Tile tile) {
        this.tiles.remove(tile.getId());
        int index = ((tile.getFloorZ()) & 0x0f << 12) | ((tile.getFloorX() & 0x0f) << 8) | (tile.getFloorY() & 0xff);
        this.tileList.remove(index);
        if (this.isInit) {
            this.hasChanged = true;
        }
    }

    @Override
    public Map<Long, Entity> getEntities() {
        return entities;
    }

    @Override
    public Map<Long, Tile> getTiles() {
        return tiles;
    }

    @Override
    public Map<Integer, Integer> getBlockExtraDataArray() {
        return this.extraData;
    }

    @Override
    public Tile getTile(int x, int y, int z) {
        int index = (z << 12) | (x << 8) | y;
        return this.tileList.containsKey(index) ? this.tileList.get(index) : null;
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
    public boolean load(boolean generate) throws IOException {
        return this.getProvider() != null && this.getProvider().getChunk(this.getX(), this.getZ(), true) != null;
    }

    @Override
    public boolean unload() throws Exception {
        return this.unload(true, true);
    }

    @Override
    public boolean unload(boolean save) throws Exception {
        return this.unload(save, true);
    }

    @Override
    public boolean unload(boolean save, boolean safe) {
        LevelProvider level = this.getProvider();
        if (level == null) {
            return true;
        }
        if (save && this.hasChanged) {
            level.saveChunk(this.getX(), this.getZ());
        }
        if (safe) {
            for (Entity entity : this.getEntities().values()) {
                if (entity instanceof Player) {
                    return false;
                }
            }
        }
        for (Entity entity : new ArrayList<>(this.getEntities().values())) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }

        for (Tile tile : new ArrayList<>(this.getTiles().values())) {
            tile.close();
        }
        this.provider = null;
        return true;
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
    public byte[] getBlockSkyLightArray() {
        return this.skyLight;
    }

    @Override
    public byte[] getBlockLightArray() {
        return this.blockLight;
    }

    @Override
    public byte[] getBiomeIdArray() {
        byte[] ids = new byte[this.getBiomeColorArray().length];
        for (int i = 0; i < this.getBiomeColorArray().length; i++) {
            int d = this.getBiomeColorArray()[i];
            ids[i] = (byte) ((d & 0xFF000000) >> 24);
        }
        return ids;
    }

    @Override
    public int[] getBiomeColorArray() {
        return this.biomeColors;
    }

    @Override
    public int[] getHeightMapArray() {
        return this.heightMap;
    }

    @Override
    public boolean hasChanged() {
        return this.hasChanged;
    }

    @Override
    public void setChanged() {
        this.setChanged(true);
    }

    @Override
    public void setChanged(boolean changed) {
        this.hasChanged = changed;
    }

    @Override
    public byte[] toFastBinary() {
        return this.toBinary();
    }

    @Override
    public boolean isLightPopulated() {
        return true;
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public void setLightPopulated(boolean value) {

    }

}
