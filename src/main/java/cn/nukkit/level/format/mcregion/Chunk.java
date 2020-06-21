package cn.nukkit.level.format.mcregion;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.palette.BiomePalette;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseRegionLoader;
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

    private CompoundTag nbt;

    public Chunk(final LevelProvider level) {
        this(level, null);
    }

    public Chunk(final Class<? extends LevelProvider> providerClass) {
        this((LevelProvider) null, null);
        this.providerClass = providerClass;
    }

    public Chunk(final Class<? extends LevelProvider> providerClass, final CompoundTag nbt) {
        this((LevelProvider) null, nbt);
        this.providerClass = providerClass;
    }

    public Chunk(final LevelProvider level, final CompoundTag nbt) {
        this.provider = level;
        if (level != null) {
            this.providerClass = level.getClass();
        }

        if (nbt == null) {
            return;
        }

        this.nbt = nbt;

        if (!(this.nbt.contains("Entities") && this.nbt.get("Entities") instanceof ListTag)) {
            this.nbt.putList(new ListTag<CompoundTag>("Entities"));
        }

        if (!(this.nbt.contains("TileEntities") && this.nbt.get("TileEntities") instanceof ListTag)) {
            this.nbt.putList(new ListTag<CompoundTag>("TileEntities"));
        }

        if (!(this.nbt.contains("TileTicks") && this.nbt.get("TileTicks") instanceof ListTag)) {
            this.nbt.putList(new ListTag<CompoundTag>("TileTicks"));
        }

        if (!(this.nbt.contains("Biomes") && this.nbt.get("Biomes") instanceof ByteArrayTag)) {
            this.nbt.putByteArray("Biomes", new byte[256]);
        }

        if (!(this.nbt.contains("HeightMap") && this.nbt.get("HeightMap") instanceof IntArrayTag)) {
            this.nbt.putIntArray("HeightMap", new int[256]);
        }

        if (!this.nbt.contains("Blocks")) {
            this.nbt.putByteArray("Blocks", new byte[32768]);
        }

        if (!this.nbt.contains("Data")) {
            this.nbt.putByteArray("Data", new byte[16384]);
            this.nbt.putByteArray("SkyLight", new byte[16384]);
            this.nbt.putByteArray("BlockLight", new byte[16384]);
        }

        final Map<Integer, Integer> extraData = new HashMap<>();

        if (!this.nbt.contains("ExtraData") || !(this.nbt.get("ExtraData") instanceof ByteArrayTag)) {
            this.nbt.putByteArray("ExtraData", Binary.writeInt(0));
        } else {
            final BinaryStream stream = new BinaryStream(this.nbt.getByteArray("ExtraData"));
            for (int i = 0; i < stream.getInt(); i++) {
                final int key = stream.getInt();
                extraData.put(key, stream.getShort());
            }
        }

        this.setPosition(this.nbt.getInt("xPos"), this.nbt.getInt("zPos"));
        this.blocks = this.nbt.getByteArray("Blocks");
        this.data = this.nbt.getByteArray("Data");
        this.skyLight = this.nbt.getByteArray("SkyLight");
        this.blockLight = this.nbt.getByteArray("BlockLight");

        if (this.nbt.contains("BiomeColors")) {
            this.biomes = new byte[16 * 16];
            final int[] biomeColors = this.nbt.getIntArray("BiomeColors");
            if (biomeColors.length == 256) {
                final BiomePalette palette = new BiomePalette(biomeColors);
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        this.biomes[x << 4 | z] = (byte) (palette.get(x, z) >> 24);
                    }
                }
            }
        } else {
            this.biomes = this.nbt.getByteArray("Biomes");
        }

        final int[] heightMap = this.nbt.getIntArray("HeightMap");
        this.heightMap = new byte[256];
        if (heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }

        if (!extraData.isEmpty()) {
            this.extraData = extraData;
        }

        this.NBTentities = ((ListTag<CompoundTag>) this.nbt.getList("Entities")).getAll();
        this.NBTtiles = ((ListTag<CompoundTag>) this.nbt.getList("TileEntities")).getAll();

        this.nbt.remove("Blocks");
        this.nbt.remove("Data");
        this.nbt.remove("SkyLight");
        this.nbt.remove("BlockLight");
        this.nbt.remove("BiomeColors");
        this.nbt.remove("HeightMap");
        this.nbt.remove("Biomes");
    }

    public static Chunk fromBinary(final byte[] data) {
        return Chunk.fromBinary(data, null);
    }

    public static Chunk fromBinary(final byte[] data, final LevelProvider provider) {
        try {
            final CompoundTag chunk = NBTIO.read(new ByteArrayInputStream(Zlib.inflate(data)), ByteOrder.BIG_ENDIAN);

            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }
            return new Chunk(provider != null ? provider : McRegion.class.newInstance(), chunk.getCompound("Level"));
        } catch (final Exception e) {
            return null;
        }
    }

    public static Chunk fromFastBinary(final byte[] data) {
        return Chunk.fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(final byte[] data, final LevelProvider provider) {
        try {
            int offset = 0;
            final Chunk chunk = new Chunk(provider != null ? provider : McRegion.class.newInstance(), null);
            chunk.provider = provider;
            final int chunkX = Binary.readInt(Arrays.copyOfRange(data, offset, offset + 3));
            offset += 4;
            final int chunkZ = Binary.readInt(Arrays.copyOfRange(data, offset, offset + 3));
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
            chunk.biomes = Arrays.copyOfRange(data, offset, offset + 256);
            offset += 256;
            final byte flags = data[offset++];
            chunk.nbt.putByte("TerrainGenerated", flags & 0b1);
            chunk.nbt.putByte("TerrainPopulated", flags >> 1 & 0b1);
            chunk.nbt.putByte("LightPopulated", flags >> 2 & 0b1);
            return chunk;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Chunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return Chunk.getEmptyChunk(chunkX, chunkZ, null);
    }

    public static Chunk getEmptyChunk(final int chunkX, final int chunkZ, final LevelProvider provider) {
        try {
            final Chunk chunk;
            if (provider != null) {
                chunk = new Chunk(provider, null);
            } else {
                chunk = new Chunk(McRegion.class, null);
            }

            chunk.setPosition(chunkX, chunkZ);
            chunk.data = new byte[16384];
            chunk.blocks = new byte[32768];
            final byte[] skyLight = new byte[16384];
            Arrays.fill(skyLight, (byte) 0xff);
            chunk.skyLight = skyLight;
            chunk.blockLight = chunk.data;

            chunk.heightMap = new byte[256];
            chunk.biomes = new byte[16 * 16];

            chunk.nbt.putByte("V", 1);
            chunk.nbt.putLong("InhabitedTime", 0);
            chunk.nbt.putBoolean("TerrainGenerated", false);
            chunk.nbt.putBoolean("TerrainPopulated", false);
            chunk.nbt.putBoolean("LightPopulated", false);

            return chunk;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getFullBlock(final int x, final int y, final int z) {
        final int i = x << 11 | z << 7 | y;
        final int block = this.blocks[i] & 0xff;
        final int data = this.data[i >> 1] & 0xff;
        if ((y & 1) == 0) {
            return block << 4 | data & 0x0f;
        } else {
            return block << 4 | data >> 4;
        }
    }

    @Override
    public Block getAndSetBlock(final int x, final int y, final int z, final Block block) {
        int i = x << 11 | z << 7 | y;
        boolean changed = false;
        final byte id = (byte) block.getId();

        final byte previousId = this.blocks[i];

        if (previousId != id) {
            this.blocks[i] = id;
            changed = true;
        }

        final int previousData;
        i >>= 1;
        final int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            previousData = old & 0x0f;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) (old & 0xf0 | block.getDamage() & 0x0f);
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        } else {
            previousData = old >> 4;
            if (Block.hasMeta[block.getId()]) {
                this.data[i] = (byte) ((block.getDamage() & 0x0f) << 4 | old & 0x0f);
                if (block.getDamage() != previousData) {
                    changed = true;
                }
            }
        }

        if (changed) {
            this.setChanged();
        }
        return Block.get(previousId, previousData);
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId) {
        return this.setBlock(x, y, z, blockId, 0);
    }

    @Override
    public boolean setBlock(final int x, final int y, final int z, final int blockId, final int meta) {
        int i = x << 11 | z << 7 | y;
        boolean changed = false;
        final byte id = (byte) blockId;
        if (this.blocks[i] != id) {
            this.blocks[i] = id;
            changed = true;
        }

        if (Block.hasMeta[blockId]) {
            i >>= 1;
            final int old = this.data[i] & 0xff;
            if ((y & 1) == 0) {
                this.data[i] = (byte) (old & 0xf0 | meta & 0x0f);
                if ((old & 0x0f) != meta) {
                    changed = true;
                }
            } else {
                this.data[i] = (byte) ((meta & 0x0f) << 4 | old & 0x0f);
                if (meta != old >> 4) {
                    changed = true;
                }
            }
        }

        if (changed) {
            this.setChanged();
        }
        return changed;
    }

    @Override
    public int getBlockId(final int x, final int y, final int z) {
        return this.blocks[x << 11 | z << 7 | y] & 0xff;
    }

    @Override
    public void setBlockId(final int x, final int y, final int z, final int id) {
        this.blocks[x << 11 | z << 7 | y] = (byte) id;
        this.setChanged();
    }

    @Override
    public int getBlockData(final int x, final int y, final int z) {
        final int b = this.data[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockData(final int x, final int y, final int z, final int data) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.data[i] & 0xff;
        if ((y & 1) == 0) {
            this.data[i] = (byte) (old & 0xf0 | data & 0x0f);
        } else {
            this.data[i] = (byte) ((data & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public int getBlockSkyLight(final int x, final int y, final int z) {
        final int sl = this.skyLight[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return sl & 0x0f;
        } else {
            return sl >> 4;
        }
    }

    @Override
    public void setBlockSkyLight(final int x, final int y, final int z, final int level) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.skyLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.skyLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.skyLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public int getBlockLight(final int x, final int y, final int z) {
        final int b = this.blockLight[x << 10 | z << 6 | y >> 1] & 0xff;
        if ((y & 1) == 0) {
            return b & 0x0f;
        } else {
            return b >> 4;
        }
    }

    @Override
    public void setBlockLight(final int x, final int y, final int z, final int level) {
        final int i = x << 10 | z << 6 | y >> 1;
        final int old = this.blockLight[i] & 0xff;
        if ((y & 1) == 0) {
            this.blockLight[i] = (byte) (old & 0xf0 | level & 0x0f);
        } else {
            this.blockLight[i] = (byte) ((level & 0x0f) << 4 | old & 0x0f);
        }
        this.setChanged();
    }

    @Override
    public boolean isPopulated() {
        return this.nbt.contains("TerrainPopulated") && this.nbt.getBoolean("TerrainPopulated");
    }

    @Override
    public void setPopulated(final boolean value) {
        this.nbt.putBoolean("TerrainPopulated", value);
        this.setChanged();
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
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
    public void setGenerated(final boolean value) {
        this.nbt.putBoolean("TerrainGenerated", value);
        this.setChanged();
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public byte[] toBinary() {
        final CompoundTag nbt = this.getNBT().copy();
        nbt.remove("BiomeColors");

        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        if (this.isGenerated()) {
            nbt.putByteArray("Blocks", this.getBlockIdArray());
            nbt.putByteArray("Data", this.getBlockDataArray());
            nbt.putByteArray("SkyLight", this.getBlockSkyLightArray());
            nbt.putByteArray("BlockLight", this.getBlockLightArray());
            nbt.putByteArray("Biomes", this.getBiomeIdArray());

            final int[] heightInts = new int[256];
            final byte[] heightBytes = this.getHeightMapArray();
            for (int i = 0; i < heightInts.length; i++) {
                heightInts[i] = heightBytes[i] & 0xFF;
            }
            nbt.putIntArray("HeightMap", heightInts);
        }

        final ArrayList<CompoundTag> entities = new ArrayList<>();
        for (final Entity entity : this.getEntities().values()) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        final ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        final ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (final BlockEntity blockEntity : this.getBlockEntities().values()) {
            blockEntity.saveNBT();
            tiles.add(blockEntity.namedTag);
        }
        final ListTag<CompoundTag> tileListTag = new ListTag<>("TileEntities");
        tileListTag.setAll(tiles);
        nbt.putList(tileListTag);

        final BinaryStream extraData = new BinaryStream();
        final Map<Integer, Integer> extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (final Integer key : extraDataArray.keySet()) {
            extraData.putInt(key);
            extraData.putShort(extraDataArray.get(key));
        }

        nbt.putByteArray("ExtraData", extraData.getBuffer());

        final CompoundTag chunk = new CompoundTag("");
        chunk.putCompound("Level", nbt);

        try {
            return Zlib.deflate(NBTIO.write(chunk, ByteOrder.BIG_ENDIAN), BaseRegionLoader.COMPRESSION_LEVEL);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLightPopulated() {
        return this.nbt.getBoolean("LightPopulated");
    }

    @Override
    public void setLightPopulated(final boolean value) {
        this.nbt.putBoolean("LightPopulated", value);
        this.setChanged();
    }

    @Override
    public void setLightPopulated() {
        this.setLightPopulated(true);
    }

    @Override
    public byte[] toFastBinary() {
        final BinaryStream stream = new BinaryStream(new byte[65536]);
        stream.put(Binary.writeInt(this.getX()));
        stream.put(Binary.writeInt(this.getZ()));
        stream.put(this.getBlockIdArray());
        stream.put(this.getBlockDataArray());
        stream.put(this.getBlockSkyLightArray());
        stream.put(this.getBlockLightArray());
        stream.put(this.getHeightMapArray());
        stream.put(this.getBiomeIdArray());
        stream.putByte((byte) ((this.isLightPopulated() ? 1 << 2 : 0) + (this.isPopulated() ? 1 << 2 : 0) + (this.isGenerated() ? 1 : 0)));
        return stream.getBuffer();
    }

    public CompoundTag getNBT() {
        return this.nbt;
    }

}
