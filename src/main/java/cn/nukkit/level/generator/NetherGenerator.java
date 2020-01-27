package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.chunk.IChunk;
import cn.nukkit.level.generator.feature.GeneratorFeature;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorGlowStone;
import cn.nukkit.level.generator.populator.impl.PopulatorGroundFire;
import cn.nukkit.level.generator.populator.impl.PopulatorLava;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.BedrockRandom;
import cn.nukkit.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.nukkit.block.BlockIds.*;

public class NetherGenerator implements Generator {

    public static GeneratorFactory FACTORY = NetherGenerator::new;
    protected final List<Populator> populators = new ArrayList<>();
    protected double lavaHeight = 32;
    protected double bedrockDepth = 5;
    protected SimplexF[] noiseGen = new SimplexF[3];
    protected List<GeneratorFeature> generatorFeatures = new ArrayList<>();

    private NetherGenerator(BedrockRandom random, String options) {
        for (int i = 0; i < noiseGen.length; i++) {
            noiseGen[i] = new SimplexF(random, 4, 1 / 4f, 1 / 64f);
        }

        PopulatorOre ores = new PopulatorOre(NETHERRACK, new OreType[]{
                new OreType(Block.get(BlockIds.QUARTZ_ORE), 20, 16, 0, 128),
                new OreType(Block.get(BlockIds.SOUL_SAND), 5, 64, 0, 128),
                new OreType(Block.get(BlockIds.GRAVEL), 5, 64, 0, 128),
                new OreType(Block.get(LAVA), 1, 16, 0, (int) this.lavaHeight),
        });
        this.populators.add(ores);

        PopulatorGroundFire groundFire = new PopulatorGroundFire();
        groundFire.setBaseAmount(1);
        groundFire.setRandomAmount(1);
        this.populators.add(groundFire);

        PopulatorLava lava = new PopulatorLava();
        lava.setBaseAmount(1);
        lava.setRandomAmount(2);
        this.populators.add(lava);
        this.populators.add(new PopulatorGlowStone());
        PopulatorOre ore = new PopulatorOre(NETHERRACK, new OreType[]{
                new OreType(Block.get(BlockIds.QUARTZ_ORE), 40, 16, 0, 128, NETHERRACK),
                new OreType(Block.get(BlockIds.SOUL_SAND), 1, 64, 30, 35, NETHERRACK),
                new OreType(Block.get(LAVA), 32, 1, 0, 32, NETHERRACK),
                new OreType(Block.get(BlockIds.MAGMA), 32, 16, 26, 37, NETHERRACK),
        });
        this.populators.add(ore);
    }

    @Override
    public String getSettings() {
        return "";
    }

    @Override
    public void generateChunk(BedrockRandom random, IChunk chunk) {
        int baseX = chunk.getX() << 4;
        int baseZ = chunk.getZ() << 4;

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                Biome biome = EnumBiome.HELL.biome;
                chunk.setBiome(x, z, biome.getId());

                chunk.setBlockId(x, 0, z, BEDROCK);
                for (int y = 115; y < 127; ++y) {
                    chunk.setBlockId(x, y, z, NETHERRACK);
                }
                chunk.setBlockId(x, 127, z, BEDROCK);
                for (int y = 1; y < 127; ++y) {
                    if (getNoise(baseX | x, y, baseZ | z) > 0) {
                        chunk.setBlockId(x, y, z, NETHERRACK);
                    } else if (y <= this.lavaHeight) {
                        chunk.setBlockId(x, y, z, LAVA);
                        chunk.setBlockLight(x, y + 1, z, (byte) 15);
                    }
                }
            }
        }
        for (GeneratorFeature feature : this.generatorFeatures) {
            feature.generate(random, chunk);
        }
    }

    @Override
    public void populateChunk(ChunkManager level, BedrockRandom random, int chunkX, int chunkZ) {
        IChunk chunk = level.getChunk(chunkX, chunkZ);

        for (Populator populator : this.populators) {
            populator.populate(level, chunkX, chunkZ, random, chunk);
        }

        Biome biome = EnumBiome.getBiome(chunk.getBiome(7, 7));
        biome.populateChunk(level, chunkX, chunkZ, random);
    }

    public Vector3f getSpawn() {
        return new Vector3f(0, 64, 0);
    }

    public float getNoise(int x, int y, int z) {
        float val = 0f;
        for (int i = 0; i < noiseGen.length; i++) {
            val += noiseGen[i].noise3D(x >> i, y, z >> i, true);
        }
        return val;
    }
}
