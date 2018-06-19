package com.nukkitx.server.level.generator.overworld;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.level.Level;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.level.data.Biome;
import com.nukkitx.api.level.data.GameRule;
import com.nukkitx.server.block.NukkitBlockState;
import com.nukkitx.server.level.biome.BiomeImpl;
import com.nukkitx.server.level.biome.impl.OceanBiome;
import com.nukkitx.server.level.biome.impl.type.GrassyBiome;
import com.nukkitx.server.level.biome.selector.BiomeSelector;
import com.nukkitx.server.level.biome.selector.overworld.OverworldBiomeSelector;
import com.nukkitx.server.level.generator.AbstractChunkGenerator;
import com.nukkitx.server.level.populator.impl.bedrock.FloorBedrockPopulator;
import gnu.trove.map.hash.TByteObjectHashMap;
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

    private static final TByteObjectHashMap<BiomeImpl> REGISTERED_BIOMES = new TByteObjectHashMap<>();
    private static final ThreadLocal<Biome[]> BIOME_CACHE = ThreadLocal.withInitial(() -> new Biome[20 * 20]);
    private static final ThreadLocal<double[]> NOISE_CACHE = ThreadLocal.withInitial(() -> new double[16 * 16 * 256]);

    static {
        REGISTERED_BIOMES.put(Biome.OCEAN.id(), new OceanBiome());
        REGISTERED_BIOMES.put(Biome.PLAINS.id(), new GrassyBiome());
    }

    private volatile Noise noiseGen;
    private volatile BiomeSelector selector;

    {
        this.addPopulator(new FloorBedrockPopulator());
    }

    @Override
    public void generateChunk(Level level, Chunk chunk, Random random) {
        //debug:
        level.getData().getGameRules().setGameRule(GameRule.SHOW_COORDINATES, true);

        super.generateChunk(level, chunk, random);
        int x = chunk.getX() << 4;
        int z = chunk.getZ() << 4;
        Biome[] biomes = this.selector.getBiomes(x - 2, z - 2, 20, 20, BIOME_CACHE.get());
        //set biomes
        for (int xx = 0; x < 16; x++)     {
            for (int zz = 0; z < 16; z++)    {
                chunk.setBiome(xx, zz, biomes[(xx << 4) | zz]);
            }
        }

        this.noiseGen.forEach(x, z, 16, 16, 4, 4, (xx, zz, v) -> {
            BiomeImpl biome = REGISTERED_BIOMES.get(biomes[(xx << 4) | zz].id());
            int min = biome == null ? 66 : biome.getMinHeight();
            for (int y = 0; y < min; y++) {
                chunk.setBlock(xx, y, zz, STONE);
            }
            int max = biome == null ? 70 : biome.getMaxHeight();
            max = min + (int) ((1.0d / (double) (max - min)) * (v * 0.5d + 0.5d));
            for (int y = min; y < max; y++) {
                chunk.setBlock(xx, y, zz, STONE);
            }
        });
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
