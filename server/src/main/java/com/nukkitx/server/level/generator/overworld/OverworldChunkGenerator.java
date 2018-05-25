package com.nukkitx.server.level.generator.overworld;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.data.Biome;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.selector.BiomeSelector;
import com.nukkitx.server.level.biome.selector.overworld.OverworldBiomeSelector;
import com.nukkitx.server.level.generator.AbstractChunkGenerator;
import com.nukkitx.server.level.populator.impl.bedrock.FloorBedrockPopulator;
import net.daporkchop.lib.noise.Noise;
import net.daporkchop.lib.noise.NoiseEngineType;

import java.util.Random;

/**
 * @author DaPorkchop_
 */
public class OverworldChunkGenerator extends AbstractChunkGenerator {
    private static final BlockState STONE = new NukkitBlockState(BlockTypes.STONE, null, null);
    private static final BlockState WATER = new NukkitBlockState(BlockTypes.STATIONARY_WATER, null, null);
    private static final BlockState AIR = new NukkitBlockState(BlockTypes.AIR, null, null);

    private static final Vector3f SPAWN = new Vector3f(0, 128, 0);

    private volatile Noise noiseGen;
    private final ThreadLocal<double[]> noiseCache = ThreadLocal.withInitial(() -> new double[16 * 16 * 256]);
    private volatile BiomeSelector selector;

    {
        this.addPopulator(new FloorBedrockPopulator());
    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {
        super.generateChunk(level, chunk, random);
        Biome[] biomes = this.selector.getBiomes(chunk.getX() << 4, chunk.getZ() << 4, 16, 16);
        //set biomes
        for (int x = 0; x < 16; x++)     {
            for (int z = 0; z < 16; z++)    {
                chunk.setBiome(x, z, biomes[(x << 4) | z]);
            }
        }
    }

    @Override
    protected void init(Level level, long seed) {
        this.noiseGen = new Noise(NoiseEngineType.PERLIN, seed ^ 982375986137953L, 8, 0.01d, 0.25d);

        this.selector = new OverworldBiomeSelector();
        this.selector.init(seed ^ 21298375986113L);
    }

    @Override
    public Vector3f getDefaultSpawn() {
        return SPAWN;
    }
}
