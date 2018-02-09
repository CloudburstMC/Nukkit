package cn.nukkit.level.format.anvil;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.stream.NBTInputStream;
import cn.nukkit.nbt.stream.NBTOutputStream;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteOrder;
import java.util.*;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseChunk {

    protected CompoundTag nbt;

    @Override
    public BaseChunk clone() {
        Chunk chunk = (Chunk) super.clone();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        NBTOutputStream out = new NBTOutputStream(baos);
        try {
            nbt.write(out);
            NBTInputStream in = new NBTInputStream(new ByteArrayInputStream(baos.toByteArray()));
            chunk.nbt = new CompoundTag();
            chunk.nbt.load(in);
        } catch (IOException e) {

        }
        return chunk;
    }

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

        cn.nukkit.level.format.ChunkSection[] sections = new cn.nukkit.level.format.ChunkSection[16];
        for (Tag section : this.nbt.getList("Sections").getAll()) {
            if (section instanceof CompoundTag) {
                int y = ((CompoundTag) section).getByte("Y");
                if (y < 16) {
                    sections[y] = new ChunkSection((CompoundTag) section);
                }
            }
        }

        for (int y = 0; y < 16; y++) {
            if (sections[y] == null) {
                sections[y] = EmptyChunkSection.EMPTY[y];
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

        this.setPosition(this.nbt.getInt("xPos"), this.nbt.getInt("zPos"));
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
        this.heightMap = new byte[256];
        if (heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }


        this.extraData = extraData;

        this.NBTentities = this.nbt.getList("Entities", CompoundTag.class).getAll();
        this.NBTtiles = this.nbt.getList("TileEntities", CompoundTag.class).getAll();

        ListTag<CompoundTag> updateEntries = nbt.getList("TileTicks", CompoundTag.class);

        if (updateEntries != null && updateEntries.size() > 0) {
            for (CompoundTag entryNBT : updateEntries.getAll()) {
                Block block = null;

                try {
                    Tag tag = entryNBT.get("i");
                    if (tag instanceof StringTag) {
                        String name = ((StringTag) tag).data;

                        @SuppressWarnings("unchecked")
                        Class<? extends Block> clazz = (Class<? extends Block>) Class.forName("cn.nukkit.block." + name);

                        Constructor constructor = clazz.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        block = (Block) constructor.newInstance();
                    }
                } catch (Throwable e) {
                    continue;
                }

                if (block == null) {
                    continue;
                }

                block.x = entryNBT.getInt("x");
                block.y = entryNBT.getInt("y");
                block.z = entryNBT.getInt("z");

                this.provider.getLevel().scheduleUpdate(block, block, entryNBT.getInt("t"), entryNBT.getInt("p"), false);
            }
        }

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

    public CompoundTag getNBT() {
        return nbt;
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

            return new Chunk(provider, chunk.getCompound("Level"));
        } catch (Exception e) {
            Server.getInstance().getLogger().logException(e);
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
        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        nbt.putIntArray("BiomeColors", this.getBiomeColorArray());
        int[] heightInts = new int[256];
        byte[] heightBytes = this.getHeightMapArray();
        for (int i = 0; i < heightInts.length; i++) {
            heightInts[i] = heightBytes[i] & 0xFF;
        }

        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            if (section instanceof EmptyChunkSection) {
                continue;
            }
            CompoundTag s = new CompoundTag(null);
            s.putByte("Y", section.getY());
            s.putByteArray("Blocks", section.getIdArray());
            s.putByteArray("Data", section.getDataArray());
            s.putByteArray("BlockLight", section.getLightArray());
            s.putByteArray("SkyLight", section.getSkyLightArray());
            nbt.getList("Sections", CompoundTag.class).add(s);
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

        Set<BlockUpdateEntry> entries = this.provider.getLevel().getPendingBlockUpdates(this);

        if (entries != null) {
            ListTag<CompoundTag> tileTickTag = new ListTag<>("TileTicks");
            long totalTime = this.provider.getLevel().getCurrentTick();

            for (BlockUpdateEntry entry : entries) {
                CompoundTag entryNBT = new CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (int) (entry.delay - totalTime))
                        .putInt("p", entry.priority);
                tileTickTag.add(entryNBT);
            }

            nbt.putList(tileTickTag);
        }

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

        nbt.putInt("xPos", this.getX());
        nbt.putInt("zPos", this.getZ());

        ListTag<CompoundTag> sectionList = new ListTag<>("Sections");
        for (cn.nukkit.level.format.ChunkSection section : this.getSections()) {
            if (section instanceof EmptyChunkSection) {
                continue;
            }
            CompoundTag s = new CompoundTag(null);
            s.putByte("Y", (section.getY()));
            s.putByteArray("Blocks", section.getIdArray());
            s.putByteArray("Data", section.getDataArray());
            s.putByteArray("BlockLight", section.getLightArray());
            s.putByteArray("SkyLight", section.getSkyLightArray());
            sectionList.add(s);
        }
        nbt.putList(sectionList);

        nbt.putIntArray("BiomeColors", this.getBiomeColorArray());
        int[] heightInts = new int[256];
        byte[] heightBytes = this.getHeightMapArray();
        for (int i = 0; i < heightInts.length; i++) {
            heightInts[i] = heightBytes[i] & 0xFF;
        }
        nbt.putIntArray("HeightMap", heightInts);

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

        Set<BlockUpdateEntry> entries = this.provider.getLevel().getPendingBlockUpdates(this);

        if (entries != null) {
            ListTag<CompoundTag> tileTickTag = new ListTag<>("TileTicks");
            long totalTime = this.provider.getLevel().getCurrentTick();

            for (BlockUpdateEntry entry : entries) {
                CompoundTag entryNBT = new CompoundTag()
                        .putString("i", entry.block.getSaveId())
                        .putInt("x", entry.pos.getFloorX())
                        .putInt("y", entry.pos.getFloorY())
                        .putInt("z", entry.pos.getFloorZ())
                        .putInt("t", (int) (entry.delay - totalTime))
                        .putInt("p", entry.priority);
                tileTickTag.add(entryNBT);
            }

            nbt.putList(tileTickTag);
        }

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

    @Override
    public int getBlockSkyLight(int x, int y, int z) {
        cn.nukkit.level.format.ChunkSection section = this.sections[y >> 4];
        if (section instanceof cn.nukkit.level.format.anvil.ChunkSection) {
            cn.nukkit.level.format.anvil.ChunkSection anvilSection = (cn.nukkit.level.format.anvil.ChunkSection) section;
            if (anvilSection.skyLight != null) {
                return section.getBlockSkyLight(x, y & 0x0f, z);
            } else if (!anvilSection.hasSkyLight) {
                return 0;
            } else {
                int height = getHighestBlockAt(x, z);
                if (height < y) {
                    return 15;
                } else if (height == y) {
                    return Block.transparent[getBlockId(x, y, z)] ? 15 : 0;
                } else {
                    return section.getBlockSkyLight(x, y & 0x0f, z);
                }
            }
        } else {
            return section.getBlockSkyLight(x, y & 0x0f, z);
        }
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        cn.nukkit.level.format.ChunkSection section = this.sections[y >> 4];
        if (section instanceof cn.nukkit.level.format.anvil.ChunkSection) {
            cn.nukkit.level.format.anvil.ChunkSection anvilSection = (cn.nukkit.level.format.anvil.ChunkSection) section;
            if (anvilSection.blockLight != null) {
                return section.getBlockLight(x, y & 0x0f, z);
            } else if (!anvilSection.hasBlockLight) {
                return 0;
            } else {
                return section.getBlockLight(x, y & 0x0f, z);
            }
        } else {
            return section.getBlockLight(x, y & 0x0f, z);
        }
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
                chunk = new Chunk(Anvil.class, null);
            }

            chunk.setPosition(chunkX, chunkZ);

            chunk.sections = new cn.nukkit.level.format.ChunkSection[16];
            for (int y = 0; y < 16; ++y) {
                chunk.sections[y] = EmptyChunkSection.EMPTY[y];
            }

            chunk.heightMap = new byte[256];
            chunk.biomeColors = new int[256];

            chunk.nbt.putByte("V", 1);
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
