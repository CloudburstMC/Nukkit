package cn.nukkit.level;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.format.generic.ChunkConverter;
import cn.nukkit.level.format.leveldb.LevelDB;
import cn.nukkit.level.format.mcregion.McRegion;
import cn.nukkit.level.format.mcregion.RegionLoader;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.LevelException;
import cn.nukkit.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LevelProviderConverter {

    private final Level level;

    private final String path;

    private LevelProvider provider;

    private Class<? extends LevelProvider> toClass;

    LevelProviderConverter(final Level level, final String path) {
        this.level = level;
        this.path = path;
    }

    private static int getChunkX(final byte[] key) {
        return key[3] << 24 |
            key[2] << 16 |
            key[1] << 8 |
            key[0];
    }

    private static int getChunkZ(final byte[] key) {
        return key[7] << 24 |
            key[6] << 16 |
            key[5] << 8 |
            key[4];
    }

    LevelProviderConverter from(final LevelProvider provider) {
        if (!(provider instanceof McRegion) && !(provider instanceof LevelDB)) {
            throw new IllegalArgumentException("From type can be only McRegion or LevelDB");
        }
        this.provider = provider;
        return this;
    }

    LevelProviderConverter to(final Class<? extends LevelProvider> toClass) {
        if (toClass != Anvil.class) {
            throw new IllegalArgumentException("To type can be only Anvil");
        }
        this.toClass = toClass;
        return this;
    }

    LevelProvider perform() throws IOException {
        new File(this.path).mkdir();
        final File dat = new File(this.provider.getPath(), "level.dat.old");
        new File(this.provider.getPath(), "level.dat").renameTo(dat);
        Utils.copyFile(dat, new File(this.path, "level.dat"));
        final LevelProvider result;
        try {
            if (this.provider instanceof LevelDB) {
                try (final FileInputStream stream = new FileInputStream(this.path + "level.dat")) {
                    stream.skip(8);
                    final CompoundTag levelData = NBTIO.read(stream, ByteOrder.LITTLE_ENDIAN);
                    if (levelData != null) {
                        NBTIO.writeGZIPCompressed(new CompoundTag().putCompound("Data", levelData), new FileOutputStream(this.path + "level.dat"));
                    } else {
                        throw new IOException("LevelData can not be null");
                    }
                } catch (final IOException e) {
                    throw new LevelException("Invalid level.dat");
                }
            }
            result = this.toClass.getConstructor(Level.class, String.class).newInstance(this.level, this.path);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.toClass == Anvil.class) {
            if (this.provider instanceof McRegion) {
                new File(this.path, "region").mkdir();
                for (final File file : new File(this.provider.getPath() + "region/").listFiles()) {
                    final Matcher m = Pattern.compile("-?\\d+").matcher(file.getName());
                    final int regionX;
                    final int regionZ;
                    try {
                        if (m.find()) {
                            regionX = Integer.parseInt(m.group());
                        } else {
                            continue;
                        }
                        if (m.find()) {
                            regionZ = Integer.parseInt(m.group());
                        } else {
                            continue;
                        }
                    } catch (final NumberFormatException e) {
                        continue;
                    }
                    final RegionLoader region = new RegionLoader(this.provider, regionX, regionZ);
                    for (final Integer index : region.getLocationIndexes()) {
                        final int chunkX = index & 0x1f;
                        final int chunkZ = index >> 5;
                        final BaseFullChunk old = region.readChunk(chunkX, chunkZ);
                        if (old == null) {
                            continue;
                        }
                        final int x = regionX << 5 | chunkX;
                        final int z = regionZ << 5 | chunkZ;
                        final FullChunk chunk = new ChunkConverter(result)
                            .from(old)
                            .to(Chunk.class)
                            .perform();
                        result.saveChunk(x, z, chunk);
                    }
                    region.close();
                }
            }
            if (this.provider instanceof LevelDB) {
                new File(this.path, "region").mkdir();
                for (final byte[] key : ((LevelDB) this.provider).getTerrainKeys()) {
                    final int x = LevelProviderConverter.getChunkX(key);
                    final int z = LevelProviderConverter.getChunkZ(key);
                    final BaseFullChunk old = ((LevelDB) this.provider).readChunk(x, z);
                    final FullChunk chunk = new ChunkConverter(result)
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

}
