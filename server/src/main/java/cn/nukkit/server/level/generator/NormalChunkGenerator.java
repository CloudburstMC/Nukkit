package cn.nukkit.server.level.generator;

import cn.nukkit.api.block.BlockState;
import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.level.Level;
import cn.nukkit.api.level.chunk.Chunk;
import cn.nukkit.api.level.chunk.generator.ChunkGenerator;
import cn.nukkit.server.block.NukkitBlockState;
import cn.nukkit.server.level.biome.NukkitBiome;
import cn.nukkit.server.math.NukkitRandom;
import com.flowpowered.math.vector.Vector3f;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class NormalChunkGenerator implements ChunkGenerator {

    private int waterHeight = 62;
    private int bedrockDepth = 5;

    private static final Vector3f SPAWN = new Vector3f(0, 7, 0);

    public NormalChunkGenerator() {

    }

    public NukkitBiome pickBiome(int x, int z) {
        // todo: pick a biome based on temperatures
        return NukkitBiome.OCEAN;
    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public void populateChunk(Level level, Chunk chunk, Random random) {

    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }

}
