package cn.nukkit.level.format.mcregion;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntArrayTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.Zlib;

import java.io.ByteArrayInputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseFullChunk {

    protected final CompoundTag nbt;

    public Chunk(LevelProvider level) {
        this(level, null);
    }

    public Chunk(Class<? extends LevelProvider> providerClass) {
        this((LevelProvider) null, null);
        this.providerClass = providerClass;
    }

    public Chunk(Class<? extends LevelProvider> providerClass, CompoundTag nbt) {
        this((LevelProvider) null, nbt);
        this.providerClass = providerClass;
    }

    public Chunk(LevelProvider level, CompoundTag nbt) {
        this.provider = level;
        if (level != null) {
            this.providerClass = level.getClass();
        }

        if (nbt == null) {
            this.nbt = new CompoundTag("Level");
            return;
        }

        this.nbt = nbt;

        if (!(this.nbt.contains("Entities") && (this.nbt.get("Entities") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("Entities"));
        }

        if (!(this.nbt.contains("TileEntities") && (this.nbt.get("TileEntities") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("TileEntities"));
        }

        if (!(this.nbt.contains("TileTicks") && (this.nbt.get("TileTicks") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("TileTicks"));
        }

        if (!(this.nbt.contains("BiomeColors") && (this.nbt.get("BiomeColors") instanceof IntArrayTag))) {
            this.nbt.putIntArray("BiomeColors", new int[256]);
        }

        if (!(this.nbt.contains("HeightMap") && (this.nbt.get("HeightMap") instanceof IntArrayTag))) {
            this.nbt.putIntArray("HeightMap", new int[256]);
        }

        if (!(this.nbt.contains("Blocks"))) {
            this.nbt.putByteArray("Blocks", new byte[32768]);
        }

        if (!(this.nbt.contains("Data"))) {
            this.nbt.putByteArray("Data", new byte[16384]);
            this.nbt.putByteArray("SkyLight", new byte[16384]);
            this.nbt.putByteArray("BlockLight", new byte[16384]);
        }

        Map<Integer, Integer> extraData = new HashMap<>();

        if (!this.nbt.contains("ExtraData") || !(this.nbt.get("ExtraData") instanceof ByteArrayTag)) {
            this.nbt.putByteArray("ExtraData", Binary.writeInt(0));
        } else {
            BinaryStream stream = new BinaryStream(this.nbt.getByteArray("ExtraData"));
            for (int i = 0; i < stream.getInt(); i++) {
                int key = stream.getInt();
                extraData.put(key, stream.getShort());
            }
        }

        this.setPosition(this.nbt.getInt("xPos"), this.nbt.getInt("zPos"));
        this.blocks = this.nbt.getByteArray("Blocks");
        this.data = this.nbt.getByteArray("Data");
        this.skyLight = this.nbt.getByteArray("SkyLight");
        this.blockLight = this.nbt.getByteArray("BlockLight");
        int[] biomeColors = this.nbt.getIntArray("BiomeColors");
        if (biomeColors.length != 256) {
            biomeColors = new int[256];
            Arrays.fill(biomeColors, Binary.readInt(new byte[]{(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00}));
        }
        this.biomeColors = biomeColors;
        int[] heightMap = this.nbt.getIntArray("HeightMap");
        this.heightMap = new byte[256];
        if (heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }

        this.extraData = extraData;

        this.NBTentities = ((ListTag<CompoundTag>) this.nbt.getList("Entities")).getAll();
        this.NBTtiles = ((ListTag<CompoundTag>) this.nbt.getList("TileEntities")).getAll();

        if (this.nbt.contains("Biomes")) {
            this.checkOldBiomes(this.nbt.getByteArray("Biomes"));
            this.nbt.remove("Biomes");
        }

        this.nbt.remove("Blocks");
        this.nbt.remove("Data");
        this.nbt.remove("SkyLight");
        this.nbt.remove("BlockLight");
        this.nbt.remove("BiomeColors");
        this.nbt.remove("HeightMap");
        this.nbt.remove("Biomes");
    }

    @Override
    public int getBlockId(int x, int y, int z) {
        return this.blocks[(x << 11) | (z << 7) | y] & 0xff;
    }

    @Override
    public void setBlockId(int x, int y, int z, int id) {
        this.blocks[(x << 11) | (z << 7) | y] = (byte) id;
        setChanged();
    }

    @Override
    public int getBlockData(int x, int y, int z) {
        int b = this.data[(x << 10) | (z << 6) | (y >> 1)] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockData(int x, int y, int z, int data) {
        int i = (x << 10) | (z << 6) | (y >> 1);
        int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            this.data[i] = (byte) ((old & 0xf0) | (data & 0x0f));
        } else {
            this.data[i] = (byte) (((data & 0x0f) << 4) | (old & 0x0f));
        }
        setChanged();
    }

    @Override
    public int getFullBlock(int x, int y, int z) {
        int i = (x << 11) | (z << 7) | y;
        int block = this.blocks[i] & 0xff;
        int data = this.data[i >> 1] & 0xff;
        if ((y & 1) == 0) {
            return (block << 4) | (data & 0x0f);
        } else {
            return (block << 4) | (data >> 4);
        }
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId) {
        return setBlock(x, y, z, blockId, 0);
    }

    @Override
    public boolean setBlock(int x, int y, int z, int blockId, int meta) {
        int i = (x << 11) | (z << 7) | y;
        boolean changed = false;
        byte id = (byte) blockId;
        if (this.blocks[i] != id) {
            this.blocks[i] = id;
            changed = true;
        }

        if (Block.hasMeta[blockId]) {
            i >>= 1;
            int old = this.data[i] & 0xff;
            if ((y & 1) == 0) {
                this.data[i] = (byte) ((old & 0xf0) | (meta & 0x0f));
                if ((old & 0x0f) != meta) {
                    changed = true;
                }
            } else {
                this.data[i] = (byte) (((meta & 0x0f) << 4) | (old & 0x0f));
                if (meta != (old >> 4)) {
                    changed = true;
                }
            }
        }

        if (changed) {
            setChanged();
        }
        return changed;
    }

    @Override
    public Block getAndSetBlock(int x, int y, int z, Block block) {
        int i = (x << 11) | (z << 7) | y;
        boolean changed = false;
        byte id = (byte) block.getId();

        byte previousId = this.blocks[i];

        if (previousId != id) {
            this.blocks[i] = id;
            changed = true;
        }

        int previousData;
        i >>= 1;
        int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            previousData = old & 0x0f;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) ((old & 0xf0) | (block.getDamage() & 0x0f));
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        } else {
            previousData = old >> 4;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) (((block.getDamage() & 0x0f) << 4) | (old & 0x0f));
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        }

        if (changed) {
            setChanged();
        }
        return Block.get(previousId, previousData);
    }

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        int sl = this.skyLight[(x << 10) | (z << 6) | (y >> 1)] & 0xff;
        if ((y & 1) == 0) {
            return sl & 0x0f;
        } else {
            return sl >> 4;
        }
    }

    @Override
    public void setBlockSkyLight(int x, int y, int z, int level) {
        int i = (x << 10) | (z << 6) | (y >> 1);
        int old = this.skyLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.skyLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.skyLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
        setChanged();
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        int b = this.blockLight[(x << 10) | (z << 6) | (y >> 1)] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        int i = (x << 10) | (z << 6) | (y >> 1);
        int old = this.blockLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.blockLight[i] = (byte) ((old & 0xf0) | (level & 0x0f));
        } else {
            this.blockLight[i] = (byte) (((level & 0x0f) << 4) | (old & 0x0f));
        }
        setChanged();
    }

    @Override
    public boolean isLightPopulated() {
        return this.nbt.getBoolean("LightPopulated");
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public void setLightPopulated(boolean value) {
        this.nbt.putBoolean("LightPopulated", value);
        setChanged();
    }

    @Override
    public boolean isPopulated() {
        return this.nbt.contains("TerrainPopulated") && this.nbt.getBoolean("TerrainPopulated");
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean value) {
        this.nbt.putBoolean("TerrainPopulated", value);
        setChanged();
    }

    @Override
    public boolean isGenerated() {
        if (this.nbt.contains("TerrainGenerated")) {
            return this.nbt.getBoolean("TerrainGenerated");
        } else if (this.nbt.contains("TerrainPopulated")) {
            return this.nbt.getBoolean("TerrainPopulated");
        }
        return false;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        this.nbt.putBoolean("TerrainGenerated", value);
        setChanged();
    }

    public static Chunk fromBinary(byte[] data) {
        return fromBinary(data, null);
    }

    public static Chunk fromBinary(byte[] data, LevelProvider provider) {
        try {
            CompoundTag chunk = NBTIO.read(new ByteArrayInputStream(Zlib.inflate(data)), ByteOrder.BIG_ENDIAN);

            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }
            return new Chunk(provider != null ? provider : McRegion.class.newInstance(), chunk.getCompound("Level"));
        } catch (Exception e) {
            return null;
        }
    }

    public static Chunk fromFastBinary(byte[] data) {
        return fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(byte[] data, LevelProvider provider) {
        try {
            int offset = 0;
            Chunk chunk = new Chunk(provider != null ? provider : McRegion.class.newInstance(), null);
            chunk.provider = provider;
            int chunkX = (Binary.readInt(Arrays.copyOfRange(data, offset, offset + 3)));
            offset += 4;
            int chunkZ = (Binary.readInt(Arrays.copyOfRange(data, offset, offset + 3)));
            chunk.setPosition(chunkX, chunkZ);
            offset += 4;
            chunk.blocks = Arrays.copyOfRange(data, offset, offset + 32767);
            offset += 32768;
            chunk.data = Arrays.copyOfRange(data, offset, offset + 16383);
            offset += 16384;
            chunk.skyLight = Arrays.copyOfRange(data, offset, offset + 16383);
            offset += 16384;
            chunk.blockLight = Arrays.copyOfRange(data, offset, offset + 16383);
            offset += 16384;
            chunk.heightMap = Arrays.copyOfRange(data, offset, offset + 256);
            offset += 256;
            chunk.biomeColors = new int[256];
            for (int i = 0; i < 256; i++) {
                chunk.biomeColors[i] = Binary.readInt(Arrays.copyOfRange(data, offset, offset + 3));
                offset += 4;
            }
            byte flags = data[offset++];
            chunk.nbt.putByte("TerrainGenerated", (flags & 0b1));
            chunk.nbt.putByte("TerrainPopulated", ((flags >> 1) & 0b1));
            chunk.nbt.putByte("LightPopulated", ((flags >> 2) & 0b1));
            return chunk;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public byte[] toFastBinary() {
        BinaryStream stream = new BinaryStream(new byte[65536]);
        stream.put(Binary.writeInt(this.getX()));
        stream.put(Binary.writeInt(this.getZ()));
        stream.put(this.getBlockIdArray());
        stream.put(this.getBlockDataArray());
        stream.put(this.getBlockSkyLightArray());
        stream.put(this.getBlockLightArray());
        stream.put(this.getHeightMapArray());
        for (int color : this.getBiomeColorArray()) {
            stream.put(Binary.writeInt(color));
        }
        stream.putByte((byte) ((this.isLightPopulated() ? 1 << 2 : 0) + (this.isPopulated() ? 1 << 2 : 0) + (this.isGenerated() ? 1 : 0)));
        return stream.getBuffer();
    }

    @Override
    public byte[] toBinary() {
        CompoundTag nbt = this.getNBT().copy();

        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        if (this.isGenerated()) {
            nbt.putByteArray("Blocks", this.getBlockIdArray());
            nbt.putByteArray("Data", this.getBlockDataArray());
            nbt.putByteArray("SkyLight", this.getBlockSkyLightArray());
            nbt.putByteArray("BlockLight", this.getBlockLightArray());
            nbt.putIntArray("BiomeColors", this.getBiomeColorArray());

            int[] heightInts = new int[256];
            byte[] heightBytes = this.getHeightMapArray();
            for (int i = 0; i < heightInts.length; i++) {
                heightInts[i] = heightBytes[i] & 0xFF;
            }
            nbt.putIntArray("HeightMap", heightInts);
        }


        ArrayList<CompoundTag> entities = new ArrayList<>();
        for (Entity entity : this.getEntities().values()) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (BlockEntity blockEntity : this.getBlockEntities().values()) {
            blockEntity.saveNBT();
            tiles.add(blockEntity.namedTag);
        }
        ListTag<CompoundTag> tileListTag = new ListTag<>("TileEntities");
        tileListTag.setAll(tiles);
        nbt.putList(tileListTag);

        BinaryStream extraData = new BinaryStream();
        Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (Integer key : extraDataArray.keySet()) {
            extraData.putInt(key);
            extraData.putShort(extraDataArray.get(key));
        }

        nbt.putByteArray("ExtraData", extraData.getBuffer());

        CompoundTag chunk = new CompoundTag("");
        chunk.putCompound("Level", nbt);

        try {
            return Zlib.deflate(NBTIO.write(chunk, ByteOrder.BIG_ENDIAN), RegionLoader.COMPRESSION_LEVEL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CompoundTag getNBT() {
        return nbt;
    }

    public static Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return getEmptyChunk(chunkX, chunkZ, null);
    }

    public static Chunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider) {
        try {
            Chunk chunk;
            if (provider != null) {
                chunk = new Chunk(provider, null);
            } else {
                chunk = new Chunk(McRegion.class, null);
            }

            chunk.setPosition(chunkX, chunkZ);
            chunk.data = new byte[16384];
            chunk.blocks = new byte[32768];
            byte[] skyLight = new byte[16384];
            Arrays.fill(skyLight, (byte) 0xff);
            chunk.skyLight = skyLight;
            chunk.blockLight = chunk.data;

            chunk.heightMap = new byte[256];
            chunk.biomeColors = new int[256];

            chunk.nbt.putByte("V", 1);
            chunk.nbt.putLong("InhabitedTime", 0);
            chunk.nbt.putBoolean("TerrainGenerated", false);
            chunk.nbt.putBoolean("TerrainPopulated", false);
            chunk.nbt.putBoolean("LightPopulated", false);

            return chunk;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
