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

    protected void generateCaveNode(long seed, FullChunk chunk, double x, double y, double z, float radius, float angelOffset, float angel, int angle, int maxAngle, double paramDouble4) {
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

        int randomAngel = localRandom.nextInt(maxAngle / 2) + maxAngle / 4;
        boolean bigAngel = localRandom.nextInt(6) == 0;

        for (; angle < maxAngle; angle++) {
            double offsetXZ = 1.5D + MathHelper.sin(angle * 3.141593F / maxAngle) * radius * 1.0F;
            double d4 = offsetXZ * paramDouble4;

            float cos = MathHelper.cos(angel);
            float sin = MathHelper.sin(angel);
            x += MathHelper.cos(angelOffset) * cos;
            y += sin;
            z += MathHelper.sin(angelOffset) * cos;

            if (bigAngel)
                angel *= 0.92F;
            else {
                angel *= 0.7F;
            }
            angel += f2 * 0.1F;
            angelOffset += f1 * 0.1F;

            f2 *= 0.9F;
            f1 *= 0.75F;
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;

            if ((!isLargeCave) && (angle == randomAngel) && (radius > 1.0F) && (maxAngle > 0)) {
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset - 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset + 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                return;
            }
            if ((!isLargeCave) && (localRandom.nextInt(4) == 0)) {
                continue;
            }

            // Check if distance to working point (x and z) too larger than working radius (maybe ??)
            double distanceX = x - realX;
            double distanceZ = z - realZ;
            double angelDiff = maxAngle - angle;
            double newRadius = radius + 2.0F + 16.0F;
            if (distanceX * distanceX + distanceZ * distanceZ - angelDiff * angelDiff > newRadius * newRadius) {
                return;
            }

            //Boundaries check.
            if ((x < realX - 16.0D - offsetXZ * 2.0D) || (z < realZ - 16.0D - offsetXZ * 2.0D) || (x > realX + 16.0D + offsetXZ * 2.0D) || (z > realZ + 16.0D + offsetXZ * 2.0D))
                continue;


            int xFrom = MathHelper.floor(x - offsetXZ) - chunkX * 16 - 1;
            int xTo = MathHelper.floor(x + offsetXZ) - chunkX * 16 + 1;

            int yFrom = MathHelper.floor(y - d4) - 1;
            int yTo = MathHelper.floor(y + d4) + 1;

            int zFrom = MathHelper.floor(z - offsetXZ) - chunkZ * 16 - 1;
            int zTo = MathHelper.floor(z + offsetXZ) - chunkZ * 16 + 1;

            if (xFrom < 0)
                xFrom = 0;
            if (xTo > 16)
                xTo = 16;

            if (yFrom < 1)
                yFrom = 1;
            if (yTo > this.worldHeightCap - 8) {
                yTo = this.worldHeightCap - 8;
            }
            if (zFrom < 0)
                zFrom = 0;
            if (zTo > 16)
                zTo = 16;

            // Search for water
            boolean waterFound = false;
            for (int xx = xFrom; (!waterFound) && (xx < xTo); xx++) {
                for (int zz = zFrom; (!waterFound) && (zz < zTo); zz++) {
                    for (int yy = yTo + 1; (!waterFound) && (yy >= yFrom - 1); yy--) {
                        if (yy >= 0 && yy < this.worldHeightCap) {
                            int block = chunk.getBlockId(xx, yy, zz);
                            if (block == Block.WATER || block == Block.STILL_WATER) {
                                waterFound = true;
                            }
                            if ((yy != yFrom - 1) && (xx != xFrom) && (xx != xTo - 1) && (zz != zFrom) && (zz != zTo - 1))
                                yy = yFrom;
                        }
                    }
                }
            }

            if (waterFound) {
                continue;
            }

            // Generate cave
            for (int xx = xFrom; xx < xTo; xx++) {
                double modX = (xx + chunkX * 16 + 0.5D - x) / offsetXZ;
                for (int zz = zFrom; zz < zTo; zz++) {
                    double modZ = (zz + chunkZ * 16 + 0.5D - z) / offsetXZ;

                    boolean grassFound = false;
                    if (modX * modX + modZ * modZ < 1.0D) {
                        for (int yy = yTo; yy > yFrom; yy--) {
                            double modY = ((yy - 1) + 0.5D - y) / d4;
                            if ((modY > -0.7D) && (modX * modX + modY * modY + modZ * modZ < 1.0D)) {
                                Biome biome = Biome.getBiome(chunk.getBiomeId(xx, zz));
                                if (!(biome instanceof CaveBiome)) {
                                    continue;
                                }

                                int material = chunk.getBlockId(xx, yy, zz);
                                int materialAbove = chunk.getBlockId(xx, yy + 1, zz);
                                if (material == Block.GRASS || material == Block.MYCELIUM) {
                                    grassFound = true;
                                }
                                //TODO: check this
//								if (this.isSuitableBlock(material, materialAbove, biome))
                                {
                                    if (yy - 1 < 10) {
                                        chunk.setBlock(xx, yy, zz, Block.LAVA);
                                    } else {
                                        chunk.setBlock(xx, yy, zz, Block.AIR);

                                        // If grass was just deleted, try to
                                        // move it down
                                        if (grassFound && (chunk.getBlockId(xx, yy - 1, zz) == Block.DIRT)) {
                                            chunk.setBlock(xx, yy - 1, zz, ((CaveBiome) biome).getSurfaceBlock());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isLargeCave) {
                break;
            }
        }
    }

    protected boolean isSuitableBlock(int block, int blockAbove, Biome biome) {
        if (!(biome instanceof CaveBiome)) {
            return false;
        }

        CaveBiome caveBiome = (CaveBiome) biome;

        if (block == caveBiome.getStoneBlock()) {
            return true;
        }

        if (block == Block.SAND || block == Block.GRAVEL) {
            return !(blockAbove == Block.WATER || blockAbove == Block.STILL_WATER || blockAbove == Block.LAVA || blockAbove == Block.STILL_LAVA);
        }

        if (block == caveBiome.getGroundBlock()) {
            return true;
        }

        if (block == caveBiome.getSurfaceBlock()) {
            return true;
        }

        // Few hardcoded cases
        if (block == Block.HARDENED_CLAY) {
            return true;
        }

        if (block == Block.SANDSTONE) {
            return true;
        }

        // TODO: add red sandstone case in Minecraft 1.8
        return block == Block.SNOW;

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