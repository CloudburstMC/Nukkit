package cn.nukkit.level.format.generic;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.ChunkSection;
import java.util.ArrayList;

public class ChunkConverter {

    private final LevelProvider provider;

    private BaseFullChunk chunk;

    private Class<? extends FullChunk> toClass;

    public ChunkConverter(final LevelProvider provider) {
        this.provider = provider;
    }

    public ChunkConverter from(final BaseFullChunk chunk) {
        if (!(chunk instanceof cn.nukkit.level.format.mcregion.Chunk) && !(chunk instanceof cn.nukkit.level.format.leveldb.Chunk)) {
            throw new IllegalArgumentException("From type can be only McRegion or LevelDB");
        }
        this.chunk = chunk;
        return this;
    }

    public ChunkConverter to(final Class<? extends FullChunk> toClass) {
        if (toClass != Chunk.class) {
            throw new IllegalArgumentException("To type can be only Anvil");
        }
        this.toClass = toClass;
        return this;
    }

    public FullChunk perform() {
        final BaseFullChunk result;
        try {
            result = (BaseFullChunk) this.toClass.getMethod("getEmptyChunk", int.class, int.class, LevelProvider.class).invoke(null, this.chunk.getX(), this.chunk.getZ(), this.provider);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        if (this.toClass == Chunk.class) {
            for (int Y = 0; Y < 8; Y++) {
                boolean empty = true;
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (this.chunk.getBlockId(x, Y << 4 | y, z) != 0) {
                                empty = false;
                                break;
                            }
                        }
                        if (!empty) {
                            break;
                        }
                    }
                    if (!empty) {
                        break;
                    }
                }
                if (!empty) {
                    final ChunkSection section = new ChunkSection(Y);
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                section.setBlockId(x, y, z, this.chunk.getBlockId(x, Y << 4 | y, z));
                                section.setBlockData(x, y, z, this.chunk.getBlockData(x, Y << 4 | y, z));
                                section.setBlockLight(x, y, z, this.chunk.getBlockLight(x, Y << 4 | y, z));
                                section.setBlockSkyLight(x, y, z, this.chunk.getBlockSkyLight(x, Y << 4 | y, z));
                            }
                        }
                    }
                    ((BaseChunk) result).sections[Y] = section;
                }
            }
        }
        System.arraycopy(this.chunk.biomes, 0, result.biomes, 0, 256);
        System.arraycopy(this.chunk.getHeightMapArray(), 0, result.heightMap, 0, 256);
        if (this.chunk.NBTentities != null && !this.chunk.NBTentities.isEmpty()) {
            result.NBTentities = new ArrayList<>(this.chunk.NBTentities.size());
            this.chunk.NBTentities.forEach(nbt -> result.NBTentities.add(nbt.copy()));
        }

        if (this.chunk.NBTtiles != null && !this.chunk.NBTtiles.isEmpty()) {
            result.NBTtiles = new ArrayList<>(this.chunk.NBTtiles.size());
            this.chunk.NBTtiles.forEach(nbt -> result.NBTtiles.add(nbt.copy()));
        }
        result.setGenerated();
        result.setPopulated();
        result.setLightPopulated();
        result.initChunk();
        return result;
    }

}
