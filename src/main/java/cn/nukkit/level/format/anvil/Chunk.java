package cn.nukkit.level.format.anvil;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.palette.BiomePalette;
import cn.nukkit.level.format.generic.BaseChunk;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.BlockUpdateEntry;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.Zlib;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Chunk extends BaseChunk {

    protected long inhabitedTime;
    protected boolean terrainPopulated;
    protected boolean terrainGenerated;

    @Override
    public Chunk clone() {
        Chunk chunk = (Chunk) super.clone();
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
            this.biomes = new BiomePalette();
            this.sections = new cn.nukkit.level.format.ChunkSection[16];
            for (int layer = 0; layer < 16; layer++) {
                this.sections[layer] = EmptyChunkSection.EMPTY[layer];
            }
            return;
        }

        this.sections = new cn.nukkit.level.format.ChunkSection[16];
        for (Tag section : nbt.getList("Sections").getAll()) {
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

        TIntIntMap extraData = new TIntIntHashMap();

        Tag extra = nbt.get("ExtraData");
        if (extra instanceof ByteArrayTag) {
            BinaryStream stream = new BinaryStream(((ByteArrayTag) extra).data);
            for (int i = 0; i < stream.getInt(); i++) {
                int key = stream.getInt();
                extraData.put(key, stream.getShort());
            }
        }

        this.setPosition(nbt.getInt("xPos"), nbt.getInt("zPos"));
        if (sections.length > SECTION_COUNT) {
            throw new ChunkException("Invalid amount of chunks");
        }

        int[] biomeColors = nbt.getIntArray("BiomeColors");
        if (biomeColors != null && biomeColors.length == 256) {
            this.biomes = new BiomePalette(biomeColors);
        } else {
            this.biomes = new BiomePalette();
        }

        int[] heightMap = nbt.getIntArray("HeightMap");
        this.heightMap = new byte[256];
        if (heightMap.length != 256) {
            Arrays.fill(this.heightMap, (byte) 255);
        } else {
            for (int i = 0; i < heightMap.length; i++) {
                this.heightMap[i] = (byte) heightMap[i];
            }
        }

        if (!extraData.isEmpty()) this.extraData = extraData;

        this.NBTentities = nbt.getList("Entities", CompoundTag.class).getAll();
        this.NBTtiles = nbt.getList("TileEntities", CompoundTag.class).getAll();
        if (this.NBTentities.isEmpty()) this.NBTentities = null;
        if (this.NBTtiles.isEmpty()) this.NBTtiles = null;

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

        if (nbt.contains("Biomes")) {
            checkOldBiomes(nbt.getByteArray("Biomes"));
        }

        this.inhabitedTime = nbt.getLong("InhabitedTime");
        this.terrainPopulated = nbt.getBoolean("TerrainPopulated");
        this.terrainGenerated = nbt.getBoolean("TerrainGenerated");
    }

    @Override
    public boolean isPopulated() {
        return this.terrainPopulated;
    }

    @Override
    public void setPopulated() {
        this.setPopulated(true);
    }

    @Override
    public void setPopulated(boolean value) {
        if (value != this.terrainPopulated) {
            this.terrainPopulated = value;
            setChanged();
        }
    }

    @Override
    public boolean isGenerated() {
        return this.terrainGenerated || this.terrainPopulated;
    }

    @Override
    public void setGenerated() {
        this.setGenerated(true);
    }

    @Override
    public void setGenerated(boolean value) {
        if (this.terrainGenerated != value) {
            this.terrainGenerated = value;
            setChanged();
        }
    }

    public CompoundTag getNBT() {
        CompoundTag tag = new CompoundTag();

        tag.put("LightPopulated", new ByteTag("LightPopulated", (byte) (isLightPopulated() ? 1 : 0)));
        tag.put("InhabitedTime", new LongTag("InhabitedTime", this.inhabitedTime));

        tag.put("V", new ByteTag("V", (byte) 1));

        tag.put("TerrainGenerated", new ByteTag("TerrainGenerated", (byte) (isGenerated() ? 1 : 0)));
        tag.put("TerrainPopulated", new ByteTag("TerrainPopulated", (byte) (isPopulated() ? 1 : 0)));

        return tag;
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
        for (Entity entity : this.getEntities().values(new Entity[0])) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (BlockEntity blockEntity : this.getBlockEntities().values(new BlockEntity[0])) {
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
        TIntIntMap extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (Integer key : extraDataArray.keys()) {
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
        for (Entity entity : this.getEntities().values(new Entity[0])) {
            if (!(entity instanceof Player) && !entity.closed) {
                entity.saveNBT();
                entities.add(entity.namedTag);
            }
        }
        ListTag<CompoundTag> entityListTag = new ListTag<>("Entities");
        entityListTag.setAll(entities);
        nbt.putList(entityListTag);

        ArrayList<CompoundTag> tiles = new ArrayList<>();
        for (BlockEntity blockEntity : this.getBlockEntities().values(new BlockEntity[0])) {
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
        TIntIntMap extraDataArray = this.getBlockExtraDataArray();
        extraData.putInt(extraDataArray.size());
        for (Integer key : extraDataArray.keys()) {
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

            chunk.heightMap = new byte[256];
            chunk.inhabitedTime = 0;
            chunk.terrainGenerated = false;
            chunk.terrainPopulated = false;
//            chunk.lightPopulated = false;
            return chunk;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean compress() {
        super.compress();
        boolean result = false;
        for (cn.nukkit.level.format.ChunkSection section : getSections()) {
            if (section instanceof ChunkSection) {
                ChunkSection anvilSection = (ChunkSection) section;
                if (!anvilSection.isEmpty()) {
                    result |= anvilSection.compress();
                }
            }
        }
        return result;
    }
}
