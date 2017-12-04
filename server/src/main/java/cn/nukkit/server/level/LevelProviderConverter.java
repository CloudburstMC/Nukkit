package cn.nukkit.server.level;

import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.level.format.LevelProvider;
import cn.nukkit.server.level.format.anvil.Anvil;
import cn.nukkit.server.level.format.anvil.Chunk;
import cn.nukkit.server.level.format.generic.BaseFullChunk;
import cn.nukkit.server.level.format.generic.ChunkConverter;
import cn.nukkit.server.level.format.leveldb.LevelDB;
import cn.nukkit.server.level.format.mcregion.McRegion;
import cn.nukkit.server.level.format.mcregion.RegionLoader;
import cn.nukkit.server.nbt.NBTIO;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.utils.LevelException;
import cn.nukkit.server.utils.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LevelProviderConverter {

    private LevelProvider provider;
    private Class<? extends LevelProvider> toClass;
    private Level level;
    private String path;

    LevelProviderConverter(Level level, String path) {
        this.level = level;
        this.path = path;
    }

    LevelProviderConverter from(LevelProvider provider) {
        if (!(provider instanceof McRegion) && !(provider instanceof LevelDB)) {
            throw new IllegalArgumentException("From type can be only McRegion or LevelDB");
        }
        this.provider = provider;
        return this;
    }

    LevelProviderConverter to(Class<? extends LevelProvider> toClass) {
        if (toClass != Anvil.class) {
            throw new IllegalArgumentException("To type can be only Anvil");
        }
        this.toClass = toClass;
        return this;
    }

    LevelProvider perform() throws IOException {
        new File(path).mkdir();
        File dat = new File(provider.getPath(), "level.dat.old");
        new File(provider.getPath(), "level.dat").renameTo(dat);
        Utils.copyFile(dat, new File(path, "level.dat"));
        LevelProvider result;
        try {
            if (provider instanceof LevelDB) {
                try (FileInputStream stream = new FileInputStream(path + "level.dat")) {
                    stream.skip(8);
                    CompoundTag levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
                    if (levelData != null) {
                        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(path + "level.dat"));
                    } else {
                        throw new IOException("LevelData can not be null");
                    }
                } catch (IOException e) {
                    throw new LevelException("Invalid level.dat");
                }
            }
            result = toClass.getConstructor(Level.class, String.class).newInstance(level, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (toClass == Anvil.class) {
            if (provider instanceof McRegion) {
                new File(path, "region").mkdir();
                for (File file : new File(provider.getPath() + "region/").listFiles()) {
                    Matcher m = Pattern.compile("-?\\d+").matcher(file.getName());
                    int regionX, regionZ;
                    try {
                        if (m.find()) {
                            regionX = Integer.parseInt(m.group());
                        } else continue;
                        if (m.find()) {
                            regionZ = Integer.parseInt(m.group());
                        } else continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    RegionLoader region = new RegionLoader(provider, regionX, regionZ);
                    for (Integer index : region.getLocationIndexes()) {
                        int chunkX = index & 0x1f;
                        int chunkZ = index >> 5;
                        BaseFullChunk old = region.readChunk(chunkX, chunkZ);
                        if (old == null) continue;
                        int x = (regionX << 5) | chunkX;
                        int z = (regionZ << 5) | chunkZ;
                        FullChunk chunk = new ChunkConverter(result)
                                .from(old)
                                .to(Chunk.class)
                                .perform();
                        result.saveChunk(x, z, chunk);
                    }
                    region.close();
                }
            }
            if (provider instanceof LevelDB) {
                new File(path, "region").mkdir();
                for (byte[] key : ((LevelDB) provider).getTerrainKeys()) {
                    int x = getChunkX(key);
                    int z = getChunkZ(key);
                    BaseFullChunk old = ((LevelDB) provider).readChunk(x, z);
                    FullChunk chunk = new ChunkConverter(result)
                            .from(old)
                            .to(Chunk.class)
                            .perform();
                    result.saveChunk(x, z, chunk);
                }
            }
            result.doGarbageCollection();
        }
        return result;
    }

    private static int getChunkX(byte[] key) {
        return (key[3] << 24) |
                (key[2] << 16) |
                (key[1] << 8) |
                key[0];
    }

    private static int getChunkZ(byte[] key) {
        return (key[7] << 24) |
                (key[6] << 16) |
                (key[5] << 8) |
                key[4];
    }
}
