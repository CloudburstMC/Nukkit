package cn.nukkit.level.format.generic;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.nbt.CompoundTag;
import cn.nukkit.nbt.DoubleTag;
import cn.nukkit.nbt.ListTag;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.ChunkException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * author: MagicDroidX
 * Nukkit Project
 */

public abstract class BaseChunk extends BaseFullChunk implements Chunk {

    protected int[] biomeColors;

    protected int[] heightMap;

    protected ChunkSection[] sections = new ChunkSection[SECTION_COUNT];

    protected List<CompoundTag> NBTtiles;

    protected List<CompoundTag> NBTentities;

    protected LevelProvider provider;

    protected int x;
    protected int z;

    private boolean isInit = false;
    protected boolean hasChanged = false;

    protected BaseChunk(LevelProvider provider, double x, double z, ChunkSection[] sections, int[] biomeColors, int[] heightMap, List<CompoundTag> entities, List<CompoundTag> tiles) throws ChunkException {
        super(provider, x, z, new byte[]{}, new byte[]{}, new byte[]{}, new byte[]{}, biomeColors, heightMap, entities, tiles);
        this.provider = provider;
        this.x = (int) x;
        this.z = (int) z;
        for (int Y = 0; Y < sections.length; ++Y) {
            ChunkSection section = sections[Y];
            if (section != null) {
                this.sections[Y] = section;
            } else {
                throw new ChunkException("Received invalid ChunkSection instance");
            }
            if (Y >= SECTION_COUNT) {
                throw new ChunkException("Invalid amount of chunks");
            }
        }
        if (biomeColors.length != 256) {
            biomeColors = new int[256];
            Arrays.fill(biomeColors, Binary.readInt(new byte[]{(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00}));
        }
        this.biomeColors = biomeColors;

        if (heightMap.length != 256) {
            heightMap = new int[256];
            Arrays.fill(heightMap, 127);
        }
        this.heightMap = heightMap;

        this.NBTtiles = tiles;
        this.NBTentities = entities;
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        return this.sections[y >> 4].getFullBlock(x, y & 0x0f, z);
    }

    @Override
    public boolean setBlock(int x, int y, int z) {
        return this.setBlock(x, y, z, null, null);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId) {
        return this.setBlock(x, y, z, blockId, null);
    }

    @Override
    public boolean setBlock(int x, int y, int z, Integer blockId, Integer meta) {
        try {
            this.hasChanged = true;
            return this.sections[y >> 4].setBlock(x, y & 0x0f, z, blockId & 0xff, meta & 0xff);
        } catch (ChunkException e) {
            LevelProvider level = this.getProvider();
            int Y = y >> 4;
            try {
                this.setInternalSection(Y, (ChunkSection) provider.getClass().getMethod("createChunkSection", int.class).invoke(provider.getClass(), Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            //this.setInternalSection(Y, level.createChunkSection(Y));
            return this.sections[y >> 4].setBlock(x, y & 0x0f, z, blockId & 0xff, meta & 0xff);
        }
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return this.sections[y >> 4].getBlockId(x, y & 0x0f, z);
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        try {
            this.sections[y >> 4].setBlockId(x, y & 0x0f, z, id);
            this.hasChanged = true;
        } catch (ChunkException e) {
            LevelProvider level = this.getProvider();
            int Y = y >> 4;
            try {
                this.setInternalSection(Y, (ChunkSection) provider.getClass().getMethod("createChunkSection", int.class).invoke(provider.getClass(), Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            //this.setInternalSection(Y, level.createChunkSection(Y));
            this.setBlockId(x, y, z, id);
        }
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        return this.sections[y >> 4].getBlockData(x, y & 0x0f, z);
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        try {
            this.sections[y >> 4].setBlockData(x, y & 0x0f, z, data);
            this.hasChanged = true;
        } catch (ChunkException e) {
            LevelProvider provider = this.getProvider();
            int Y = y >> 4;
            try {
                this.setInternalSection(Y, (ChunkSection) provider.getClass().getMethod("createChunkSection", int.class).invoke(provider.getClass(), Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            this.setBlockData(x, y, z, data);
        }
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockSkyLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        try {
            this.sections[y >> 4].setBlockSkyLight(x, y & 0x0f, z, level);
            this.hasChanged = true;
        } catch (ChunkException e) {
            LevelProvider provider = this.getProvider();
            int Y = y >> 4;
            try {
                this.setInternalSection(Y, (ChunkSection) provider.getClass().getMethod("createChunkSection", int.class).invoke(provider.getClass(), Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            this.setBlockSkyLight(x, y, z, level);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.sections[y >> 4].getBlockLight(x, y & 0x0f, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        try {
            this.sections[y >> 4].setBlockLight(x, y & 0x0f, z, level);
            this.hasChanged = true;
        } catch (ChunkException e) {
            LevelProvider provider = this.getProvider();
            int Y = y >> 4;
            try {
                this.setInternalSection(Y, (ChunkSection) provider.getClass().getMethod("createChunkSection", int.class).invoke(provider.getClass(), Y));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e1) {
                e1.printStackTrace();
            }
            this.setBlockLight(x, y, z, level);
        }
    }

    @Override
    public byte[] getBlockIdColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(128);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockIdColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockDataColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockSkyLightColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightColumn(int x, int z) {
        ByteBuffer buffer = ByteBuffer.allocate(64);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockLightColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public boolean isSectionEmpty(float fY) {
        return this.sections[(int) fY] instanceof EmptyChunkSection;
    }

    @Override
    public ChunkSection getSection(float fY) {
        return this.sections[(int) fY];
    }

    @Override
    public boolean setSection(float fY, ChunkSection section) {
        byte[] emptyIdArray = new byte[4096];
        byte[] emptyDataArray = new byte[2048];
        Arrays.fill(emptyIdArray, (byte) 0x00);
        Arrays.fill(emptyDataArray, (byte) 0x00);
        if (Arrays.equals(emptyIdArray, section.getIdArray()) && Arrays.equals(emptyDataArray, section.getDataArray())) {
            this.sections[(int) fY] = new EmptyChunkSection((int) fY);
        } else {
            this.sections[(int) fY] = section;
        }
        this.hasChanged = true;
        return true;
    }

    private void setInternalSection(float fY, ChunkSection section) {
        this.sections[(int) fY] = section;
        this.hasChanged = true;
    }

    @Override
    public byte[] getBlockIdArray() {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockIdColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockDataArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockDataColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockSkyLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockSkyLightColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public byte[] getBlockLightArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        for (int y = 0; y < SECTION_COUNT; y++) {
            buffer.put(this.sections[y].getBlockLightColumn(x, z));
        }
        /*byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes, 0, bytes.length);
        return bytes;*/
        return buffer.array();
    }

    @Override
    public ChunkSection[] getSections() {
        return sections;
    }


    protected void checkOldBiomes(byte[] data) {
        if (data.length != 256) {
            return;
        }
        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = Biome.getBiome(data[(z << 4) + x]);
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
                    if (((int) ((DoubleTag) pos.get(0)).data >> 4) != this.x || (((int) ((DoubleTag) pos.get(2)).data >> 4) != this.z)) {
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
                    if (!nbt.contains("id")) {
                        this.setChanged();
                        continue;
                    }
                    if (((int) nbt.getDouble("x") >> 4) != this.x || (((int) nbt.getDouble("z") >> 4) != this.z)) {
                        changed = true;
                        continue;
                    }
                    Tile tile = Tile.createTile(nbt.getString("id"), this, nbt);
                    if (tile == null) {
                        changed = true;
                        continue;
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
        return (this.biomeColors[(z << 4) + x] & 0xFF000000) >> 24;
    }

    @Override
    public void setBiomeId(int x, int z, int biomeId) {
        this.hasChanged = true;
        this.biomeColors[(z << 4) + x] = (this.biomeColors[(z << 4) + x] & 0xFFFFFF) | (biomeId << 24);
    }

    @Override
    public int[] getBiomeColor(int x, int z) {
        int color = this.biomeColors[(z << 4) | x] & 0xFFFFFF;
        return new int[]{color >> 16, (color >> 8) & 0xFF, color & 0xFF};
    }

    @Override
    public void setBiomeColor(int x, int z, int R, int G, int B) {
        this.hasChanged = true;
        this.biomeColors[(z << 4) + x] = (this.biomeColors[(z << 4) + x] & 0xFF000000) | ((R & 0xFF) << 16) | ((G & 0xFF) << 8) | (B & 0XFF);
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
    public boolean unload(boolean save, boolean safe) throws Exception {
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
        for (Entity entity : this.getEntities().values()) {
            if (entity instanceof Player) {
                continue;
            }
            entity.close();
        }
        this.getTiles().values().forEach(cn.nukkit.tile.Tile::close);
        this.provider = null;
        return true;
    }

    @Override
    public byte[] getBiomeIdArray() {
        byte[] ids = new byte[this.biomeColors.length];
        for (int i = 0; i < this.biomeColors.length; i++) {
            int d = this.biomeColors[i];
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
    public byte[] toFastBinary() throws Exception {
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
