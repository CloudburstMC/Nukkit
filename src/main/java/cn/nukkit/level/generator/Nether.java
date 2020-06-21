package cn.nukkit.level.generator;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.Level;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.noise.nukkit.f.SimplexF;
import cn.nukkit.level.generator.object.ore.OreType;
import cn.nukkit.level.generator.populator.impl.PopulatorGlowStone;
import cn.nukkit.level.generator.populator.impl.PopulatorGroundFire;
import cn.nukkit.level.generator.populator.impl.PopulatorLava;
import cn.nukkit.level.generator.populator.impl.PopulatorOre;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import java.util.*;

public class Nether extends Generator {

    private final List<Populator> populators = new ArrayList<>();

    private final double lavaHeight = 32;

    private final double bedrockDepth = 5;

    private final SimplexF[] noiseGen = new SimplexF[3];

    private final List<Populator> generationPopulators = new ArrayList<>();

    private ChunkManager level;

    /**
     * @var Random
     */
    private NukkitRandom nukkitRandom;

    private Random random;

    private long localSeed1;

    private long localSeed2;

    public Nether() {
        this(new HashMap<>());
    }

    public Nether(final Map<String, Object> options) {
        //Nothing here. Just used for future update.
    }

    @Override
    public int getId() {
        return Generator.TYPE_NETHER;
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    @Override
    public void init(final ChunkManager level, final NukkitRandom random) {
        this.level = level;
        this.nukkitRandom = random;
        this.random = new Random();
        this.nukkitRandom.setSeed(this.level.getSeed());

        for (int i = 0; i < this.noiseGen.length; i++) {
            this.noiseGen[i] = new SimplexF(this.nukkitRandom, 4, 1 / 4f, 1 / 64f);
        }

        this.nukkitRandom.setSeed(this.level.getSeed());
        this.localSeed1 = this.random.nextLong();
        this.localSeed2 = this.random.nextLong();

        final PopulatorOre ores = new PopulatorOre(BlockID.NETHERRACK);
        ores.setOreTypes(new OreType[]{
            new OreType(Block.get(BlockID.QUARTZ_ORE), 20, 16, 0, 128),
            new OreType(Block.get(BlockID.SOUL_SAND), 5, 64, 0, 128),
            new OreType(Block.get(BlockID.GRAVEL), 5, 64, 0, 128),
            new OreType(Block.get(BlockID.LAVA), 1, 16, 0, (int) this.lavaHeight),
        });
        this.populators.add(ores);

        final PopulatorGroundFire groundFire = new PopulatorGroundFire();
        groundFire.setBaseAmount(1);
        groundFire.setRandomAmount(1);
        this.populators.add(groundFire);

        final PopulatorLava lava = new PopulatorLava();
        lava.setBaseAmount(1);
        lava.setRandomAmount(2);
        this.populators.add(lava);
        this.populators.add(new PopulatorGlowStone());
        final PopulatorOre ore = new PopulatorOre(BlockID.NETHERRACK);
        ore.setOreTypes(new OreType[]{
            new OreType(Block.get(BlockID.QUARTZ_ORE), 40, 16, 0, 128, BlockID.NETHERRACK),
            new OreType(Block.get(BlockID.SOUL_SAND), 1, 64, 30, 35, BlockID.NETHERRACK),
            new OreType(Block.get(BlockID.LAVA), 32, 1, 0, 32, BlockID.NETHERRACK),
            new OreType(Block.get(BlockID.MAGMA), 32, 16, 26, 37, BlockID.NETHERRACK),
        });
        this.populators.add(ore);
    }

    @Override
    public void generateChunk(final int chunkX, final int chunkZ) {
        final int baseX = chunkX << 4;
        final int baseZ = chunkZ << 4;
        this.nukkitRandom.setSeed(chunkX * this.localSeed1 ^ chunkZ * this.localSeed2 ^ this.level.getSeed());

        final BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);

        for (int x = 0; x < 16; ++x) {
            for (int z = 0; z < 16; ++z) {
                final Biome biome = EnumBiome.HELL.biome;
                chunk.setBiomeId(x, z, biome.getId());

                chunk.setBlockId(x, 0, z, BlockID.BEDROCK);
                for (int y = 115; y < 127; ++y) {
                    chunk.setBlockId(x, y, z, BlockID.NETHERRACK);
                }
                chunk.setBlockId(x, 127, z, BlockID.BEDROCK);
                for (int y = 1; y < 127; ++y) {
                    if (this.getNoise(baseX | x, y, baseZ | z) > 0) {
                        chunk.setBlockId(x, y, z, BlockID.NETHERRACK);
                    } else if (y <= this.lavaHeight) {
                        chunk.setBlockId(x, y, z, BlockID.STILL_LAVA);
                        chunk.setBlockLight(x, y + 1, z, 15);
                    }
                }
            }
        }
        for (final Populator populator : this.generationPopulators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }
    }

    @Override
    public void populateChunk(final int chunkX, final int chunkZ) {
        final BaseFullChunk chunk = this.level.getChunk(chunkX, chunkZ);
        this.nukkitRandom.setSeed(0xdeadbeef ^ chunkX << 8 ^ chunkZ ^ this.level.getSeed());
        for (final Populator populator : this.populators) {
            populator.populate(this.level, chunkX, chunkZ, this.nukkitRandom, chunk);
        }

        final Biome biome = EnumBiome.getBiome(chunk.getBiomeId(7, 7));
        biome.populateChunk(this.level, chunkX, chunkZ, this.nukkitRandom);
    }

    @Override
    public Map<String, Object> getSettings() {
        return new HashMap<>();
    }

    @Override
    public String getName() {
        return "nether";
    }

    @Override
    public Vector3 getSpawn() {
        return new Vector3(0, 64, 0);
    }

    @Override
    public ChunkManager getChunkManager() {
        return this.level;
    }

    public float getNoise(final int x, final int y, final int z) {
        float val = 0f;
        for (int i = 0; i < this.noiseGen.length; i++) {
            val += this.noiseGen[i].noise3D(x >> i, y, z >> i, true);
        }
        return val;
    }

}
