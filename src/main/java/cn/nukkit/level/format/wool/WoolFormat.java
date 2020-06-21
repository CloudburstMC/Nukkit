package cn.nukkit.level.format.wool;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntitySpawnable;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.server.WoolLevelSaveRequestEvent;
import cn.nukkit.level.GameRules;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.ChunkSection;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.BaseLevelProvider;
import cn.nukkit.level.format.generic.BaseRegionLoader;
import cn.nukkit.level.format.generic.EmptyChunkSection;
import cn.nukkit.level.format.wool.ex.CorruptedWorldException;
import cn.nukkit.level.format.wool.ex.NewerFormatException;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.level.generator.Normal;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.*;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.BinaryStream;
import cn.nukkit.utils.ChunkException;
import cn.nukkit.utils.ThreadCache;
import com.github.luben.zstd.Zstd;

import java.io.*;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Predicate;

import static cn.nukkit.level.format.wool.WoolFormatConverter.readCompoundTag;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoolFormat extends BaseLevelProvider {

    public WoolFormat(String path, String name, CompoundTag levelData) {
        super(null, path, name, true);
        this._internalName = name;
        this.levelData = levelData;
    }

    public WoolFormat(final Level level, final String path, String name) {
        super(level, path, name, true);
        this._internalName = name;
        File file = new File(path + "/" + name + ".wool");
        if (file.exists()){
            try {
                byte[] serializedWorld = WoolFormatConverter.loadWoolWorld(file);
                deserialize(name,serializedWorld);
            } catch (IOException | CorruptedWorldException e) {
                e.printStackTrace();
            }
        }
    }

    public WoolFormat(final Level level, final String name, byte[] serializedWorld) {
        super(level, name, true);
        this._internalName = name;
        try {
            deserialize(name,serializedWorld);
        } catch (IOException | CorruptedWorldException e) {
            e.printStackTrace();
        }
    }

    private void deserialize(String worldName, byte[] serializedWorld) throws IOException, CorruptedWorldException {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(serializedWorld));

        try {
            byte[] fileHeader = new byte[WoolFormatConverter.HEADER.length];
            dataStream.read(fileHeader);

            if (!Arrays.equals(WoolFormatConverter.HEADER, fileHeader)) {
                throw new CorruptedWorldException(worldName);
            }

            // File version
            byte version = dataStream.readByte();

            // Chunk
            short minX = dataStream.readShort();
            short minZ = dataStream.readShort();

            short width = dataStream.readShort();
            short depth = dataStream.readShort();

            if (width <= 0 || depth <= 0) {
                throw new CorruptedWorldException(worldName);
            }

            int bitmaskSize = (int) Math.ceil((width * depth) / 8.0D);

            byte[] chunkBitmask = new byte[bitmaskSize];
            dataStream.read(chunkBitmask);
            BitSet chunkBitset = BitSet.valueOf(chunkBitmask);

            int compressedChunkDataLength = dataStream.readInt();
            int chunkDataLength = dataStream.readInt();
            byte[] compressedChunkData = new byte[compressedChunkDataLength];
            byte[] chunkData = new byte[chunkDataLength];

            dataStream.read(compressedChunkData);


            // Tile Entities
            byte[] compressedTileEntities = new byte[0];
            byte[] tileEntities = new byte[0];

            boolean hasTiles = dataStream.readBoolean();

            if (hasTiles){
                int compressedTileEntitiesLength = dataStream.readInt();
                int tileEntitiesLength = dataStream.readInt();
                compressedTileEntities = new byte[compressedTileEntitiesLength];
                tileEntities = new byte[tileEntitiesLength];

                dataStream.read(compressedTileEntities);
            }

            // Entities
            byte[] compressedEntities = new byte[0];
            byte[] entities = new byte[0];

            boolean hasEntities = dataStream.readBoolean();

            if (hasEntities) {
                int compressedEntitiesLength = dataStream.readInt();
                int entitiesLength = dataStream.readInt();
                compressedEntities = new byte[compressedEntitiesLength];
                entities = new byte[entitiesLength];

                dataStream.read(compressedEntities);
            }


            // Level Data NBT tag
            int compressedLevelDataLength = dataStream.readInt();
            int levelDataLength = dataStream.readInt();
            byte[] compressedLevelData = new byte[compressedLevelDataLength];
            byte[] levelData = new byte[levelDataLength];

            dataStream.read(compressedLevelData);

            // Data decompression
            Zstd.decompress(chunkData, compressedChunkData);
            Zstd.decompress(tileEntities, compressedTileEntities);
            Zstd.decompress(entities, compressedEntities);
            Zstd.decompress(levelData, compressedLevelData);

            // Chunk deserialization
            Map<Long, WoolChunk> chunks = WoolFormatConverter.readChunks(this, minX, minZ, width, depth, chunkBitset, chunkData);

            // Tile Entity deserialization
            CompoundTag tileEntitiesCompound = readCompoundTag(tileEntities);

            if (tileEntitiesCompound != null) {
                CompoundTag tag = tileEntitiesCompound.getCompound("tiles");

                for (Tag xtag : tag.getAllTags()){
                    CompoundTag tileEntityCompound = (CompoundTag) xtag;
                    int chunkX = ((IntTag) tileEntityCompound.get("x")).data >> 4;
                    int chunkZ = ((IntTag) tileEntityCompound.get("z")).data >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    WoolChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException(worldName);
                    }

                    BlockEntity.createBlockEntity(tileEntityCompound.getString("id"), chunk, tileEntityCompound);
                }
            }

            // Entity deserialization
            CompoundTag entitiesCompound = readCompoundTag(entities);

            if (entitiesCompound != null) {
                CompoundTag tag = entitiesCompound.getCompound("entities");

                for (Tag xtag : tag.getAllTags()){
                    CompoundTag entityCompound = (CompoundTag) xtag;
                    ListTag<DoubleTag> listTag = entityCompound.getList("Pos",DoubleTag.class);

                    int chunkX = WoolFormatConverter.floor(listTag.get(0).data) >> 4;
                    int chunkZ = WoolFormatConverter.floor(listTag.get(2).data) >> 4;
                    long chunkKey = ((long) chunkZ) * Integer.MAX_VALUE + ((long) chunkX);
                    WoolChunk chunk = chunks.get(chunkKey);

                    if (chunk == null) {
                        throw new CorruptedWorldException(worldName);
                    }

                    Entity.createEntity(entityCompound.getString("id"), chunk, entityCompound);
                }
            }

            // Extra Data
            CompoundTag levelDataCompound = readCompoundTag(levelData);
            if (levelDataCompound == null) levelDataCompound = new CompoundTag("", new HashMap<>());

            for (WoolChunk chunk : chunks.values()) {
                this.setChunk(chunk.getX(),chunk.getZ(),chunk);
                for (CompoundTag nbTtile : chunk.getNBTtiles()) {
                    System.out.println(nbTtile.toString());
                }
            }
            this.levelData = levelDataCompound;
        } catch (EOFException ex) {
            throw new CorruptedWorldException(worldName, ex);
        }
    }

    public static String getProviderName() {
        return "wool";
    }

    public static byte getProviderOrder() {
        return LevelProvider.ORDER_YZX;
    }

    public static boolean usesChunkSection() {
        return true;
    }

    public static boolean isValid(final String path) {
        final File worldNameDir = new File(path);
        if (!worldNameDir.exists()) {
            return false;
        }
        final File[] files = worldNameDir.listFiles();
        if (files == null) {
            return false;
        }
        for (final File file : files) {
            if (file.getName().endsWith(".wool")) {
                return true;
            }
        }
        return false;
    }


    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator) throws IOException {
        WoolFormat.generate(path, name, seed, generator, new HashMap<>());
    }

    public static void generate(final String path, final String name, final long seed, final Class<? extends Generator> generator, final Map<String, String> options) throws IOException {
        File folder = new File(path);
        if (!folder.exists()){
            folder.mkdirs();
        }
        File file = new File(path + "/" + name + ".wool");
        if (!file.exists()) {
            file.createNewFile();
        }

        final CompoundTag levelData = new CompoundTag("Data")
            .putCompound("gameRules", new CompoundTag())
            .putLong("dayTime", 0)
            .putInt("gameType", 0)
            .putBoolean("hardcore", false)
            .putBoolean("initialized", true)
            .putLong("lastPlayed", System.currentTimeMillis() / 1000)
            .putString("levelName", name)
            .putBoolean("raining", false)
            .putInt("rainTime", 0)
            .putLong("seed", seed)
            .putDouble("spawnX", 0)
            .putDouble("spawnY", 100)
            .putDouble("spawnZ", 0)
            .putBoolean("thundering", false)
            .putInt("thunderTime", 0)
            .putLong("time", 0);

        WoolFormat format = new WoolFormat(path,name,levelData);
        format.setChunk(0,0,format.getEmptyChunk(0,0));
        format.getChunk(0,0).setBlockAt(0,100,0,1,0);
        format.saveLevelData();
    }

    protected String _internalName;

    public static WoolChunkSection createChunkSection(final int y) {
        return new WoolChunkSection(y);
    }

    @Override
    public AsyncTask requestChunkTask(final int x, final int z) throws ChunkException {
        final WoolChunk chunk = (WoolChunk) this.getChunk(x, z, true);
        if (chunk == null) {
            throw new ChunkException("Invalid WoolChunk Set");
        }

        final long timestamp = chunk.getChanges();

        byte[] blockEntities = new byte[0];

        if (!chunk.getBlockEntities().isEmpty()) {
            final List<CompoundTag> tagList = new ArrayList<>();

            for (final BlockEntity blockEntity : chunk.getBlockEntities().values()) {
                if (blockEntity instanceof BlockEntitySpawnable) {
                    tagList.add(((BlockEntitySpawnable) blockEntity).getSpawnCompound());
                }
            }

            try {
                blockEntities = NBTIO.write(tagList, ByteOrder.LITTLE_ENDIAN, true);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        final Map<Integer, Integer> extra = chunk.getBlockExtraDataArray();
        final BinaryStream extraData;
        if (!extra.isEmpty()) {
            extraData = new BinaryStream();
            extraData.putVarInt(extra.size());
            for (final Map.Entry<Integer, Integer> entry : extra.entrySet()) {
                extraData.putVarInt(entry.getKey());
                extraData.putLShort(entry.getValue());
            }
        } else {
            extraData = null;
        }

        final BinaryStream stream = ThreadCache.binaryStream.get().reset();
        int count = 0;
        final cn.nukkit.level.format.ChunkSection[] sections = chunk.getSections();
        for (int i = sections.length - 1; i >= 0; i--) {
            if (!sections[i].isEmpty()) {
                count = i + 1;
                break;
            }
        }
//        stream.putByte((byte) count);  count is now sent in packet
        for (int i = 0; i < count; i++) {
            sections[i].writeTo(stream);
        }
//        for (byte height : chunk.getHeightMapArray()) {
//            stream.putByte(height);
//        } computed client side?
        stream.put(chunk.getBiomeIdArray());
        stream.putByte((byte) 0);
        if (extraData != null) {
            stream.put(extraData.getBuffer());
        } else {
            stream.putVarInt(0);
        }
        stream.put(blockEntities);

        this.getLevel().chunkRequestCallback(timestamp, x, z, count, stream.getBuffer());

        return null;
    }

    public byte[] serialize() throws IOException {
        List<WoolChunk> sortedChunks;

        if (chunks != null){
            synchronized (chunks) {
                sortedChunks = new ArrayList<>();
                chunks.values().stream().parallel().forEach(fullChunk -> sortedChunks.add((WoolChunk) fullChunk));
            }
        } else {
            sortedChunks = new ArrayList<>();
        }

        sortedChunks.removeIf(chunk -> chunk == null || Arrays.stream(chunk.getSections()).allMatch(s -> {
            if (s == null){
                return true;
            }
            for (byte b : s.getIdArray()) {
                if (b != 0) {
                    return false;
                }
            }
            return true;
        }));
        sortedChunks.sort(Comparator.comparingLong(chunk -> (long) chunk.getZ() * Integer.MAX_VALUE + (long) chunk.getX()));

        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        DataOutputStream outStream = new DataOutputStream(outByteStream);

        // HEADER
        outStream.write(WoolFormatConverter.HEADER);

        // VERSION
        outStream.writeByte(WoolFormatConverter.VERSION);

        int minX = sortedChunks.stream().mapToInt(FullChunk::getX).min().orElse(0);
        int minZ = sortedChunks.stream().mapToInt(FullChunk::getZ).min().orElse(0);
        int maxX = sortedChunks.stream().mapToInt(FullChunk::getX).max().orElse(0);
        int maxZ = sortedChunks.stream().mapToInt(FullChunk::getZ).max().orElse(0);

        outStream.writeShort(minX);
        outStream.writeShort(minZ);

        short width = (short) (maxX - minX + 1);
        short depth = (short) (maxZ - minZ + 1);

        outStream.writeShort(width);
        outStream.writeShort(depth);

        BitSet bitSet = new BitSet(width * depth);

        for (FullChunk chunk : sortedChunks) {
            int index = (chunk.getZ() - minZ) * width + (chunk.getX() - minX);

            bitSet.set(index, true);
        }

        int chunkMaskSize = (int) Math.ceil((width * depth) / 8.0D);
        WoolFormatConverter.writeBitSetAsBytes(outStream,bitSet, chunkMaskSize);

        byte[] chunkBytes = WoolFormatConverter.serializeChunks(sortedChunks);
        byte[] compressedChunkData = Zstd.compress(chunkBytes);

        outStream.writeInt(compressedChunkData.length);
        outStream.writeInt(chunkBytes.length);
        outStream.write(compressedChunkData);

        // Tile Entities
        List<CompoundTag> tileEntitiesList = new ArrayList<>();
        sortedChunks.stream().forEach(chunk -> chunk.getBlockEntities().values().stream().forEach(blockEntity -> {
            blockEntity.saveNBT();
            tileEntitiesList.add(blockEntity.namedTag);
        }));

        outStream.writeBoolean(!tileEntitiesList.isEmpty());

        if (!tileEntitiesList.isEmpty()){
            CompoundTag tileEntitiesCompound = new CompoundTag("");
            tileEntitiesCompound.putCompound("tiles",new CompoundTag());

            int i = 0;
            for (CompoundTag tag : tileEntitiesList) {
                tileEntitiesCompound.getCompound("tiles").putCompound(String.valueOf(i), tag);
                i++;
            }

            byte[] tileEntitiesData = WoolFormatConverter.serializeCompoundTag(tileEntitiesCompound);
            byte[] compressedTileEntitiesData = Zstd.compress(tileEntitiesData);

            outStream.writeInt(compressedTileEntitiesData.length);
            outStream.writeInt(tileEntitiesData.length);
            outStream.write(compressedTileEntitiesData);
        }

        // Entities
        List<CompoundTag> entitiesList = new ArrayList<>();
        sortedChunks.stream().forEach(chunk -> chunk.getEntities().values().stream().forEach(entity -> {
            entity.saveNBT();
            entitiesList.add(entity.namedTag);

        }));

        outStream.writeBoolean(!entitiesList.isEmpty());

        if (!entitiesList.isEmpty()) {
            CompoundTag entitiesCompound = new CompoundTag("");
            entitiesCompound.putCompound("entities",new CompoundTag());

            int i = 0;
            for (CompoundTag tag : entitiesList) {
                entitiesCompound.getCompound("entities").putCompound(String.valueOf(i), tag);
                i++;
            }


            byte[] entitiesData = WoolFormatConverter.serializeCompoundTag(entitiesCompound);
            byte[] compressedEntitiesData = Zstd.compress(entitiesData);

            outStream.writeInt(compressedEntitiesData.length);
            outStream.writeInt(entitiesData.length);
            outStream.write(compressedEntitiesData);
        }

        byte[] levelDataBytes = WoolFormatConverter.serializeCompoundTag(levelData != null ? levelData : new CompoundTag());
        byte[] levelDataBytesCompressed = Zstd.compress(levelDataBytes);
        int uncompressedLevelDataSize = levelDataBytes.length;
        int compressedLevelDataSize = levelDataBytesCompressed.length;

        outStream.writeInt(compressedLevelDataSize);
        outStream.writeInt(uncompressedLevelDataSize);
        outStream.write(levelDataBytesCompressed);

        return outByteStream.toByteArray();
    }


    @Override
    public BaseFullChunk getLoadedChunk(final int chunkX, final int chunkZ) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getX() == chunkX && tmp.getZ() == chunkZ) {
            return tmp;
        }
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(index));
        }
        return tmp;
    }


    @Override
    public BaseFullChunk getLoadedChunk(long hash) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getIndex() == hash) {
            return tmp;
        }
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(hash));
        }
        return tmp;
    }

    @Override
    public boolean loadChunk(int chunkX, int chunkZ, boolean create) {
        final long index = Level.chunkHash(chunkX, chunkZ);
        synchronized (this.chunks) {
            if (this.chunks.containsKey(index)) {
                return true;
            }
        }
        return this.loadChunk(index, chunkX, chunkZ, create) != null;
    }

    @Override
    public void doGarbageCollection() {
    }

    @Override
    public WoolChunk getEmptyChunk(final int chunkX, final int chunkZ) {
        return WoolChunk.getEmptyChunk(chunkX, chunkZ, this);
    }

    @Override
    public synchronized void saveChunk(final int x, final int z) {

    }

    @Override
    public synchronized void saveChunk(final int x, final int z, final FullChunk chunk) {
        this.setChunk(x,z,chunk);
    }

    @Override
    public void doGarbageCollection(final long time) {
    }

    @Override
    public synchronized BaseFullChunk loadChunk(final long index, final int chunkX, final int chunkZ, final boolean create) {
        return getEmptyChunk(chunkX,chunkZ);
    }

    @Override
    public void unloadChunks() {
    }

    @Override
    public boolean unloadChunk(int X, int Z) {
        return false;
    }

    @Override
    public boolean unloadChunk(int X, int Z, boolean safe) {
        return false;
    }

    @Override
    public BaseFullChunk getChunk(int x, int z, boolean create) {
        BaseFullChunk tmp = this.lastChunk.get();
        if (tmp != null && tmp.getX() == x && tmp.getZ() == z) {
            return tmp;
        }
        final long index = Level.chunkHash(x, z);
        synchronized (this.chunks) {
            this.lastChunk.set(tmp = this.chunks.get(index));
        }
        if (tmp != null) {
            return tmp;
        } else {
            if (create){
                tmp = getEmptyChunk(x,z);
                this.putChunk(index,tmp);
                return tmp;
            }
            return null;
        }
    }

    @Override
    public void saveChunks() {
    }

    @Override
    public boolean isRaining() {
        return this.levelData.getBoolean("raining");
    }

    @Override
    public void setRaining(boolean raining) {
        this.levelData.putBoolean("raining", raining);
    }

    @Override
    public int getRainTime() {
        return this.levelData.getInt("rainTime");
    }

    @Override
    public void setRainTime(int rainTime) {
        this.levelData.putInt("rainTime",rainTime);
    }

    @Override
    public boolean isThundering() {
        return this.levelData.getBoolean("thundering");
    }

    @Override
    public void setThundering(boolean thundering) {
        this.levelData.putBoolean("thundering",thundering);
    }

    @Override
    public int getThunderTime() {
        return this.levelData.getInt("thunderTime");
    }

    @Override
    public void setThunderTime(int thunderTime) {
        this.levelData.putInt("thunderTime",thunderTime);
    }

    @Override
    public long getCurrentTick() {
        return this.levelData.getLong("time");
    }

    @Override
    public void setCurrentTick(long currentTick) {
        this.levelData.putLong("time",currentTick);
    }

    @Override
    public long getTime() {
        return this.levelData.getLong("dayTime");
    }

    @Override
    public void setTime(long value) {
        this.levelData.putLong("dayTime",value);
    }

    @Override
    public long getSeed() {
        return this.levelData.getLong("seed");
    }

    @Override
    public void setSeed(long value) {
        this.levelData.putLong("seed",value);
    }

    @Override
    public Vector3 getSpawn() {
        if (this.spawn == null){
            return new Vector3(0,100,0);
        }
        return spawn;
    }

    @Override
    public void setSpawn(Vector3 pos) {
        this.levelData.putDouble("spawnX",pos.x);
        this.levelData.putDouble("spawnY",pos.y);
        this.levelData.putDouble("spawnZ",pos.z);
        this.spawn = pos;
    }

    @Override
    public void saveLevelData() {
        try {
            if (getPath() == null){
                WoolLevelSaveRequestEvent requestEvent = new WoolLevelSaveRequestEvent(this);
                Server.getInstance().getPluginManager().callEvent(requestEvent);
                return;
            }
            File file = new File(getPath() + "/" + _internalName + ".wool");
            RandomAccessFile rand = new RandomAccessFile(file,"rw");
            rand.seek(0);
            rand.setLength(0);
            rand.write(serialize());
            rand.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public GameRules getGamerules() {
        final GameRules rules = GameRules.getDefault();

        if (this.levelData.contains("gameRules")) {
            rules.readNBT(this.levelData.getCompound("gameRules"));
        }

        return rules;
    }

    @Override
    public String getName() {
        return this.levelData.getString("levelName");
    }

    @Override
    public void setGameRules(GameRules rules) {
        this.levelData.putCompound("gameRules", rules.writeNBT());
    }

    @Override
    public void updateLevelName(String name) {
        if (!this.getName().equals(name)) {
            this.levelData.putString("levelName", name);
        }
    }

}
