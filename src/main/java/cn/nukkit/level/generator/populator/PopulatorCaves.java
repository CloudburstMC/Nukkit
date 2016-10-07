package cn.nukkit.level.generator.populator;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.biome.Biome;
import cn.nukkit.level.generator.biome.CaveBiome;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

import java.util.Random;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class PopulatorCaves extends Populator {

    protected int checkAreaSize = 8;

    private Random random;

    public static int caveRarity = 2;//7
    public static int caveFrequency = 20;//40
    public static int caveMinAltitude = 8;
    public static int caveMaxAltitude = 128;
    public static int individualCaveRarity = 20;//25
    public static int caveSystemFrequency = 1;
    public static int caveSystemPocketChance = 0;
    public static int caveSystemPocketMinSize = 0;
    public static int caveSystemPocketMaxSize = 4;
    public static boolean evenCaveDistribution = false;

    public int worldHeightCap = 128;

    public ChunkManager chunk;

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random) {
        this.random = new Random();
        this.random.setSeed(level.getSeed());
        long worldLong1 = this.random.nextLong();
        long worldLong2 = this.random.nextLong();

        int size = this.checkAreaSize;

        for (int x = chunkX - size; x <= chunkX + size; x++)
            for (int z = chunkZ - size; z <= chunkZ + size; z++) {
                long randomX = x * worldLong1;
                long randomZ = z * worldLong2;
                this.random.setSeed(randomX ^ randomZ ^ level.getSeed());
                generateChunk(x, z, level.getChunk(chunkX, chunkZ));
            }
    }

    protected void generateLargeCaveNode(long seed, FullChunk chunk, double x, double y, double z) {
        generateCaveNode(seed, chunk, x, y, z, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void generateCaveNode(long seed, FullChunk chunk, double x, double y, double z, float paramFloat1, float paramFloat2, float paramFloat3, int angle, int maxAngle, double paramDouble4) {
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();

        double realX = chunkX * 16 + 8;
        double realZ = chunkZ * 16 + 8;

        float f1 = 0.0F;
        float f2 = 0.0F;

        Random localRandom = new Random(seed);

        if (maxAngle <= 0) {
            int checkAreaSize = this.checkAreaSize * 16 - 16;
            maxAngle = checkAreaSize - localRandom.nextInt(checkAreaSize / 4);
        }
        boolean isLargeCave = false;

        if (angle == -1) {
            angle = maxAngle / 2;
            isLargeCave = true;
        }

        int j = localRandom.nextInt(maxAngle / 2) + maxAngle / 4;
        int k = localRandom.nextInt(6) == 0 ? 1 : 0;

        for (; angle < maxAngle; angle++) {
            double d3 = 1.5D + MathHelper.sin(angle * 3.141593F / maxAngle) * paramFloat1 * 1.0F;
            double d4 = d3 * paramDouble4;

            float f3 = MathHelper.cos(paramFloat3);
            float f4 = MathHelper.sin(paramFloat3);
            x += MathHelper.cos(paramFloat2) * f3;
            y += f4;
            z += MathHelper.sin(paramFloat2) * f3;

            if (k != 0)
                paramFloat3 *= 0.92F;
            else {
                paramFloat3 *= 0.7F;
            }
            paramFloat3 += f2 * 0.1F;
            paramFloat2 += f1 * 0.1F;

            f2 *= 0.9F;
            f1 *= 0.75F;
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;

            if ((!isLargeCave) && (angle == j) && (paramFloat1 > 1.0F) && (maxAngle > 0)) {
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F,
                        paramFloat2 - 1.570796F, paramFloat3 / 3.0F, angle, maxAngle, 1.0D);
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F,
                        paramFloat2 + 1.570796F, paramFloat3 / 3.0F, angle, maxAngle, 1.0D);
                return;
            }
            if ((!isLargeCave) && (localRandom.nextInt(4) == 0)) {
                continue;
            }

            // Check if distance to working point (x and z) too larger than working radius (maybe ??)
            double d5 = x - realX;
            double d6 = z - realZ;
            double d7 = maxAngle - angle;
            double d8 = paramFloat1 + 2.0F + 16.0F;
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
                return;
            }

            //Boundaries check.
            if ((x < realX - 16.0D - d3 * 2.0D) || (z < realZ - 16.0D - d3 * 2.0D) || (x > realX + 16.0D + d3 * 2.0D) || (z > realZ + 16.0D + d3 * 2.0D))
                continue;


            int m = MathHelper.floor(x - d3) - chunkX * 16 - 1;
            int n = MathHelper.floor(x + d3) - chunkX * 16 + 1;

            int i1 = MathHelper.floor(y - d4) - 1;
            int i2 = MathHelper.floor(y + d4) + 1;

            int i3 = MathHelper.floor(z - d3) - chunkZ * 16 - 1;
            int i4 = MathHelper.floor(z + d3) - chunkZ * 16 + 1;

            if (m < 0)
                m = 0;
            if (n > 16)
                n = 16;

            if (i1 < 1)
                i1 = 1;
            if (i2 > this.worldHeightCap - 8) {
                i2 = this.worldHeightCap - 8;
            }
            if (i3 < 0)
                i3 = 0;
            if (i4 > 16)
                i4 = 16;

            // Search for water
            boolean waterFound = false;
            for (int local_x = m; (!waterFound) && (local_x < n); local_x++) {
                for (int local_z = i3; (!waterFound) && (local_z < i4); local_z++) {
                    for (int local_y = i2 + 1; (!waterFound) && (local_y >= i1 - 1); local_y--) {
                        if (local_y >= 0 && local_y < this.worldHeightCap) {
                            int material = chunk.getBlockId(local_x, local_y, local_z);
                            if (material == Block.WATER
                                    || material == Block.STILL_WATER) {
                                waterFound = true;
                            }
                            if ((local_y != i1 - 1) && (local_x != m) && (local_x != n - 1) && (local_z != i3) && (local_z != i4 - 1))
                                local_y = i1;
                        }
                    }
                }
            }
            if (waterFound)
                continue;

            // Generate cave
            for (int local_x = m; local_x < n; local_x++) {
                double d9 = (local_x + chunkX * 16 + 0.5D - x) / d3;
                for (int local_z = i3; local_z < i4; local_z++) {

                    double d10 = (local_z + chunkZ * 16 + 0.5D - z) / d3;


                    boolean grassFound = false;
                    if (d9 * d9 + d10 * d10 < 1.0D) {
                        for (int local_y = i2; local_y > i1; local_y--) {
                            double d11 = ((local_y - 1) + 0.5D - y) / d4;
                            if ((d11 > -0.7D) && (d9 * d9 + d11 * d11 + d10 * d10 < 1.0D)) {
                                Biome biome = Biome.getBiome(chunk.getBiomeId(local_x, local_z));
                                if (!(biome instanceof CaveBiome)) {
                                    continue;
                                }

                                int material = chunk.getBlockId(local_x, local_y, local_z);
                                int materialAbove = chunk.getBlockId(local_x, local_y + 1, local_z);
                                if (material == Block.GRASS || material == Block.MYCELIUM) {
                                    grassFound = true;
                                }
//								if (this.isSuitableBlock(material, materialAbove, biome))
                                {
                                    if (local_y - 1 < 10) {
                                        chunk.setBlock(local_x, local_y, local_z, Block.LAVA);
                                    } else {
                                        chunk.setBlock(local_x, local_y, local_z, Block.AIR);

                                        // If grass was just deleted, try to
                                        // move it down
                                        if (grassFound && (chunk.getBlockId(local_x, local_y - 1, local_z) == Block.DIRT)) {
                                            chunk.setBlock(local_x, local_y - 1, local_z, ((CaveBiome) biome).getSurfaceBlock());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (isLargeCave)
                break;
        }
    }

    protected boolean isSuitableBlock(int material, int materialAbove, CaveBiome biome) {
        if (material == biome.getStoneBlock()) {
            return true;
        }
        if (material == Block.SAND || material == Block.GRAVEL) {
            return !(materialAbove == Block.WATER || materialAbove == Block.STILL_WATER ||
                    materialAbove == Block.LAVA || materialAbove == Block.STILL_LAVA);
        }
        if (material == biome.getGroundBlock()) {
            return true;
        }
        if (material == biome.getSurfaceBlock()) {
            return true;
        }

        // Few hardcoded cases
        if (material == Block.HARDENED_CLAY) {
            return true;
        }
        if (material == Block.SANDSTONE) {
            return true;
        }
        // TODO: add red sandstone case in Minecraft 1.8
        return material == Block.SNOW;

    }

    protected void generateChunk(int chunkX, int chunkZ, FullChunk generatingChunkBuffer) {
        int i = this.random.nextInt(this.random.nextInt(this.random.nextInt(caveFrequency) + 1) + 1);
        if (evenCaveDistribution)
            i = caveFrequency;
        if (this.random.nextInt(100) >= caveRarity)
            i = 0;

        for (int j = 0; j < i; j++) {
            double x = chunkX * 16 + this.random.nextInt(16);

            double y;

            if (evenCaveDistribution)
                y = numberInRange(random, caveMinAltitude, caveMaxAltitude);
            else
                y = this.random.nextInt(this.random.nextInt(caveMaxAltitude - caveMinAltitude + 1) + 1) + caveMinAltitude;

            double z = chunkZ * 16 + this.random.nextInt(16);

            int count = caveSystemFrequency;
            boolean largeCaveSpawned = false;
            if (this.random.nextInt(100) <= individualCaveRarity) {
                generateLargeCaveNode(this.random.nextLong(), generatingChunkBuffer, x, y, z);
                largeCaveSpawned = true;
            }

            if ((largeCaveSpawned) || (this.random.nextInt(100) <= caveSystemPocketChance - 1)) {
                count += numberInRange(random, caveSystemPocketMinSize, caveSystemPocketMaxSize);
            }
            while (count > 0) {
                count--;
                float f1 = this.random.nextFloat() * 3.141593F * 2.0F;
                float f2 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
                float f3 = this.random.nextFloat() * 2.0F + this.random.nextFloat();

                generateCaveNode(this.random.nextLong(), generatingChunkBuffer, x, y, z, f3, f1, f2, 0, 0, 1.0D);
            }
        }
    }

    public static int numberInRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}