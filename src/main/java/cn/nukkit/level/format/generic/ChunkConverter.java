package cn.nukkit.level.format.generic;

import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.level.format.anvil.Chunk;
import cn.nukkit.level.format.anvil.ChunkSection;

import java.util.ArrayList;

public class ChunkConverter {

    private BaseFullChunk chunk;
    private Class<? extends FullChunk> toClass;
    private LevelProvider provider;

    public ChunkConverter(LevelProvider provider) {
        this.provider = provider;
    }

    public ChunkConverter from(BaseFullChunk chunk) {
        if (!(chunk instanceof cn.nukkit.level.format.mcregion.Chunk) && !(chunk instanceof cn.nukkit.level.format.leveldb.Chunk)) {
            throw new IllegalArgumentException("From type can be only McRegion or LevelDB");
        }
        this.chunk = chunk;
        return this;
    }

    public ChunkConverter to(Class<? extends FullChunk> toClass) {
        if (toClass != Chunk.class) {
            throw new IllegalArgumentException("To type can be only Anvil");
        }
        this.toClass = toClass;
        return this;
    }

    public FullChunk perform() {
        BaseFullChunk result;
        try {
            result = (BaseFullChunk) toClass.getMethod("getEmptyChunk", int.class, int.class, LevelProvider.class).invoke(null, chunk.getX(), chunk.getZ(), provider);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (toClass == Chunk.class) {
            for (int Y = 0; Y < 8; Y++) {
                boolean empty = true;
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 16; y++) {
                        for (int z = 0; z < 16; z++) {
                            if (chunk.getBlockId(x, (Y << 4) | y, z) != 0) {
                                empty = false;
                                break;
                            }
                        }
                        if (!empty) break;
                    }
                    if (!empty) break;
                }
                if (!empty) {
                    ChunkSection section = new ChunkSection(Y);
                    for (int x = 0; x < 16; x++) {
                        for (int y = 0; y < 16; y++) {
                            for (int z = 0; z < 16; z++) {
                                section.setBlockId(x, y, z, chunk.getBlockId(x, (Y << 4) | y, z));
                                section.setBlockData(x, y, z, chunk.getBlockData(x, (Y << 4) | y, z));
                                section.setBlockLight(x, y, z, chunk.getBlockLight(x, (Y << 4) | y, z));
                                section.setBlockSkyLight(x, y, z, chunk.getBlockSkyLight(x, (Y << 4) | y, z));
                            }
                        }
                    }
                    ((BaseChunk) result).sections[Y] = section;
                }
            }
        }
        System.arraycopy(chunk.biomes, 0, result.biomes, 0, 256);
        System.arraycopy(chunk.getHeightMapArray(), 0, result.heightMap, 0, 256);
        if (chunk.NBTentities != null && !chunk.NBTentities.isEmpty()) {
            result.NBTentities = new ArrayList<>(chunk.NBTentities.size());
            chunk.NBTentities.forEach((nbt) -> result.NBTentities.add(nbt.copy()));
        }

        if (chunk.NBTtiles != null && !chunk.NBTtiles.isEmpty()) {
            result.NBTtiles = new ArrayList<>(chunk.NBTtiles.size());
            chunk.NBTtiles.forEach((nbt) -> result.NBTtiles.add(nbt.copy()));
        }
        result.setGenerated();
        result.setPopulated();
        result.setLightPopulated();
        result.initChunk();
        return result;
    }
}
