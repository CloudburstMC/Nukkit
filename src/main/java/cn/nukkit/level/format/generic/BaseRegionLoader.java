package cn.nukkit.level.format.generic;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.utils.Utils;
import cn.nukkit.utils.collection.ConvertingMapWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;

/**
 * @author MagicDroidX (Nukkit Project)
 */
abstract public class BaseRegionLoader {
    public static final int VERSION = 1;
    public static final byte COMPRESSION_GZIP = 1;
    public static final byte COMPRESSION_ZLIB = 2;
    public static final int MAX_SECTOR_LENGTH = 256 << 12;
    public static final int COMPRESSION_LEVEL = 7;

    protected int x;
    protected int z;
    protected int lastSector;
    protected LevelProvider levelProvider;

    private RandomAccessFile randomAccessFile;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected final Int2ObjectMap<int[]> primitiveLocationTable = new Int2ObjectOpenHashMap<>();
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Integer boxing was polluting the memory heap", replaceWith = "primitiveLocationTable")
    protected final Map<Integer, Integer[]> locationTable = new ConvertingMapWrapper<>(
            primitiveLocationTable,
            table-> Arrays.stream(table).mapToInt(Integer::intValue).toArray(),
            table -> Arrays.stream(table).boxed().toArray(Integer[]::new)
    );

    public long lastUsed;

    public BaseRegionLoader(LevelProvider level, int regionX, int regionZ, String ext) {
        try {
            this.x = regionX;
            this.z = regionZ;
            this.levelProvider = level;
            String filePath = this.levelProvider.getPath() + "region/r." + regionX + "." + regionZ + "." + ext;
            File file = new File(filePath);
            boolean exists = file.exists();
            if (!exists) {
                file.createNewFile();
            }
            // TODO: buffering is a temporary solution to chunk reading/writing being poorly optimized
            //  - need to fix the code where it reads single bytes at a time from disk
            this.randomAccessFile = new RandomAccessFile(filePath, "rw");
            if (!exists) {
                this.createBlank();
            } else {
                this.loadLocationTable();
            }

            this.lastUsed = System.currentTimeMillis();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void compress() {
        // TODO
    }

    public RandomAccessFile getRandomAccessFile() {
        return randomAccessFile;
    }

    protected abstract boolean isChunkGenerated(int index);

    public abstract BaseFullChunk readChunk(int x, int z) throws IOException;

    protected abstract BaseFullChunk unserializeChunk(byte[] data);

    public abstract boolean chunkExists(int x, int z);

    protected abstract void saveChunk(int x, int z, byte[] chunkData) throws IOException;

    public abstract void removeChunk(int x, int z);

    public abstract void writeChunk(FullChunk chunk) throws Exception;

    public void close() throws IOException {
        if (randomAccessFile != null) randomAccessFile.close();
    }

    protected abstract void loadLocationTable() throws IOException;

    public abstract int doSlowCleanUp() throws Exception;

    protected abstract void writeLocationIndex(int index) throws IOException;

    protected abstract void createBlank() throws IOException;

    public abstract int getX();

    public abstract int getZ();

    @Deprecated
    @DeprecationDetails(
            since = "1.4.0.0-PN", by = "PowerNukkit",
            reason = "Unnecessary int-boxing causing heap pollution", 
            replaceWith = "getIntLocationIndexes()")
    public Integer[] getLocationIndexes() {
        return this.primitiveLocationTable.keySet().toArray(Utils.EMPTY_INTEGERS);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int[] getIntLocationIndexes() {
        return this.primitiveLocationTable.keySet().toIntArray();
    }
}
