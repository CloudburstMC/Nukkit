package cn.nukkit.level.format.anvil;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.tile.Tile;
import cn.nukkit.utils.Binary;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Zlib;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterInputStream;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseChunk {

    protected CompoundTag nbt;

    public Chunk(LevelProvider level) {
        this(level, null);
    }

    public Chunk(LevelProvider level, CompoundTag nbt) {
        if (nbt == null) {
            this.provider = level;
            this.nbt = new CompoundTag("Level");
            return;
        }
        this.nbt = nbt;

        if (!(this.nbt.contains("Entities") && (this.nbt.get("Entities") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("Entities"));
        }

        if (!(this.nbt.contains("TileEntities") && (this.nbt.get("TileEntities") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("Entities"));
        }

        if (!(this.nbt.contains("TileTicks") && (this.nbt.get("TileTicks") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("TileTicks"));
        }

        if (!(this.nbt.contains("Sections") && (this.nbt.get("Sections") instanceof ListTag))) {
            this.nbt.putList(new ListTag<CompoundTag>("Sections"));
        }

        if (!(this.nbt.contains("BiomeColors") && (this.nbt.get("BiomeColors") != null))) {
            this.nbt.putIntArray("BiomeColors", new int[256]);
        }

        if (!(this.nbt.contains("HeightMap") && (this.nbt.get("HeightMap") instanceof IntArrayTag))) {
            this.nbt.putIntArray("HeightMap", new int[256]);
        }

        cn.nukkit.level.format.ChunkSection[] sections = new cn.nukkit.level.format.ChunkSection[8];
        for (Tag section : this.nbt.getList("Sections").getAll()) {
            if (section instanceof CompoundTag) {
                int y = ((CompoundTag) section).getByte("Y") & 0xff;
                if (y < 8) {
                    sections[y] = new ChunkSection((CompoundTag) section);
                }
            }
        }

        for (int y = 0; y < 8; y++) {
            if (sections[y] == null) {
                sections[y] = new EmptyChunkSection(y);
            }
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

        this.provider = level;
        this.x = this.nbt.getInt("xPos");
        this.z = this.nbt.getInt("zPos");
        for (int Y = 0; Y < sections.length; ++Y) {
            cn.nukkit.level.format.ChunkSection section = sections[Y];
            if (section != null) {
                this.sections[Y] = section;
            } else {
                throw new ChunkException("Received invalid ChunkSection instance");
            }
            if (Y >= SECTION_COUNT) {
                throw new ChunkException("Invalid amount of chunks");
            }
        }

        int[] biomeColors = this.nbt.getIntArray("BiomeColors");
        if (biomeColors.length != 256) {
            biomeColors = new int[256];
            Arrays.fill(biomeColors, Binary.readInt(new byte[]{(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00}));
        }
        this.biomeColors = biomeColors;

        int[] heightMap = this.nbt.getIntArray("HeightMap");
        if (heightMap.length != 256) {
            heightMap = new int[256];
            Arrays.fill(heightMap, 127);
        }
        this.heightMap = heightMap;

        this.extraData = extraData;

        this.NBTentities = ((ListTag<CompoundTag>) this.nbt.getList("Entities")).getAll();
        this.NBTtiles = ((ListTag<CompoundTag>) this.nbt.getList("TileEntities")).getAll();

        if (this.nbt.contains("Biomes")) {
            this.checkOldBiomes(this.nbt.getByteArray("Biomes"));
            this.nbt.remove("Biomes");
        }

        this.nbt.remove("Sections");
        this.nbt.remove("ExtraData");
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
        this.hasChanged = true;
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
        this.hasChanged = true;
    }

    public CompoundTag getNBT() {
        return nbt;
    }

    public static Chunk fromBinary(byte[] data) {
        return fromBinary(data, null);
    }

    public static Chunk fromBinary(byte[] data, LevelProvider provider) {
        try {
            CompoundTag chunk = NBTIO.read(new InflaterInputStream(new ByteArrayInputStream(data)), ByteOrder.BIG_ENDIAN);

            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }

            return new Chunk(provider, chunk.getCompound("Level"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Chunk fromFastBinary(byte[] data) {
        return fromFastBinary(data, null);
    }

    public static Chunk fromFastBinary(byte[] data, LevelProvider provider) {
        try {
            CompoundTag chunk = NBTIO.read(new DataInputStream(new ByteArrayInputStream(data)), ByteOrder.BIG_ENDIAN);
            if (!chunk.contains("Level") || !(chunk.get("Level") instanceof CompoundTag)) {
                return null;
            }

            return new Chunk(provider, chunk.getCompound("Level"));
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public byte[] toFastBinary() {
        CompoundTag nbt = this.getNBT().copy();
        nbt.putInt("xPos", this.x);
        nbt.putInt("zPos", this.z);

        nbt.putIntArray("BiomeColors", this.getBiomeColorArray());
        nbt.putIntArray("HeightMap", this.getHeightMapArray());

        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            if (section instanceof EmptyChunkSection) {
                continue;
            }
            CompoundTag s = new CompoundTag(null);
            s.putByte("Y", (byte) (section.getY() & 0xff));
            s.putByteArray("Blocks", section.getIdArray());
            s.putByteArray("Data", section.getDataArray());
            s.putByteArray("BlockLight", section.getLightArray());
            s.putByteArray("SkyLight", section.getSkyLightArray());
            nbt.getList("Sections", CompoundTag.class).add(section.getY(), s);
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
        for (Tile tile : this.getTiles().values()) {
            tile.saveNBT();
            tiles.add(tile.namedTag);
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
            return NBTIO.write(chunk, ByteOrder.BIG_ENDIAN);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public byte[] toBinary() {
        CompoundTag nbt = this.getNBT().copy();

        nbt.putInt("xPos", this.x);
        nbt.putInt("zPos", this.z);

        ListTag<CompoundTag> sectionList = new ListTag<>("Sections");
        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            if (section instanceof EmptyChunkSection) {
                continue;
            }
            CompoundTag s = new CompoundTag(null);
            s.putByte("Y", (byte) (section.getY()));
            s.putByteArray("Blocks", section.getIdArray());
            s.putByteArray("Data", section.getDataArray());
            s.putByteArray("BlockLight", section.getLightArray());
            s.putByteArray("SkyLight", section.getSkyLightArray());
            sectionList.add(section.getY(), s);
        }
        nbt.putList(sectionList);

        nbt.putIntArray("BiomeColors", this.getBiomeColorArray());
        nbt.putIntArray("HeightMap", this.getHeightMapArray());

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
        for (Tile tile : this.getTiles().values()) {
            tile.saveNBT();
            tiles.add(tile.namedTag);
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

    public static Chunk getEmptyChunk(int chunkX, int chunkZ) {
        return getEmptyChunk(chunkX, chunkZ, null);
    }

    public static Chunk getEmptyChunk(int chunkX, int chunkZ, LevelProvider provider) {
        try {
            //Chunk chunk = new Chunk(provider != null ? provider : Anvil.class.newInstance(), null);
            Chunk chunk = new Chunk(provider, null);
            chunk.x = chunkX;
            chunk.z = chunkZ;

            chunk.sections = new cn.nukkit.level.format.ChunkSection[8];
            for (int y = 0; y < 8; ++y) {
                chunk.sections[y] = new EmptyChunkSection(y);
            }

            chunk.heightMap = new int[256];
            chunk.biomeColors = new int[256];

            chunk.nbt.putByte("V", (byte) 1);
            chunk.nbt.putLong("InhabitedTime", 0);
            chunk.nbt.putBoolean("TerrainGenerated", false);
            chunk.nbt.putBoolean("TerrainPopulated", false);
            chunk.nbt.putBoolean("LightPopulated", false);

            return chunk;
        } catch (Exception e) {
            return null;
        }
    }
}
