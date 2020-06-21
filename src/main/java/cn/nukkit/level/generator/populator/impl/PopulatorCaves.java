package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.BlockID;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.EnumBiome;
import cn.nukkit.level.biome.type.CoveredBiome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;
import java.util.Random;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class PopulatorCaves extends Populator {

    public static int caveRarity = 7;//7

    public static int caveFrequency = 40;//40

    public static int caveMinAltitude = 8;

    public static int caveMaxAltitude = 67;

    public static int individualCaveRarity = 25;//25

    public static int caveSystemFrequency = 1;

    public static int caveSystemPocketChance = 0;

    public static int caveSystemPocketMinSize = 0;

    public static int caveSystemPocketMaxSize = 4;

    public static boolean evenCaveDistribution = false;

    public int worldHeightCap = 128;

    protected int checkAreaSize = 8;

    private Random random;

    public static int numberInRange(final Random random, final int min, final int max) {
        return min + random.nextInt(max - min + 1);
    }

    @Override
    public void populate(final ChunkManager level, final int chunkX, final int chunkZ, final NukkitRandom random, final FullChunk chunk) {
        this.random = new Random();
        this.random.setSeed(level.getSeed());
        final long worldLong1 = this.random.nextLong();
        final long worldLong2 = this.random.nextLong();

        final int size = this.checkAreaSize;

        for (int x = chunkX - size; x <= chunkX + size; x++) {
            for (int z = chunkZ - size; z <= chunkZ + size; z++) {
                final long randomX = x * worldLong1;
                final long randomZ = z * worldLong2;
                this.random.setSeed(randomX ^ randomZ ^ level.getSeed());
                this.generateChunk(x, z, chunk);
            }
        }
    }

    protected void generateLargeCaveNode(final long seed, final FullChunk chunk, final double x, final double y, final double z) {
        this.generateCaveNode(seed, chunk, x, y, z, 1.0F + this.random.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5D);
    }

    protected void generateCaveNode(final long seed, final FullChunk chunk, double x, double y, double z, final float radius, float angelOffset, float angel, int angle, int maxAngle, final double scale) {
        final int chunkX = chunk.getX();
        final int chunkZ = chunk.getZ();

        final double realX = chunkX * 16 + 8;
        final double realZ = chunkZ * 16 + 8;

        float f1 = 0.0F;
        float f2 = 0.0F;

        final Random localRandom = new Random(seed);

        if (maxAngle <= 0) {
            final int checkAreaSize = this.checkAreaSize * 16 - 16;
            maxAngle = checkAreaSize - localRandom.nextInt(checkAreaSize / 4);
        }
        boolean isLargeCave = false;

        if (angle == -1) {
            angle = maxAngle / 2;
            isLargeCave = true;
        }

        final int randomAngel = localRandom.nextInt(maxAngle / 2) + maxAngle / 4;
        final boolean bigAngel = localRandom.nextInt(6) == 0;

        for (; angle < maxAngle; angle++) {
            final double offsetXZ = 1.5D + MathHelper.sin(angle * 3.141593F / maxAngle) * radius * 1.0F;
            final double offsetY = offsetXZ * scale;

            final float cos = MathHelper.cos(angel);
            final float sin = MathHelper.sin(angel);
            x += MathHelper.cos(angelOffset) * cos;
            y += sin;
            z += MathHelper.sin(angelOffset) * cos;

            if (bigAngel) {
                angel *= 0.92F;
            } else {
                angel *= 0.7F;
            }
            angel += f2 * 0.1F;
            angelOffset += f1 * 0.1F;

            f2 *= 0.9F;
            f1 *= 0.75F;
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;

            if (!isLargeCave && angle == randomAngel && radius > 1.0F && maxAngle > 0) {
                this.generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset - 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                this.generateCaveNode(localRandom.nextLong(), chunk, x, y, z, localRandom.nextFloat() * 0.5F + 0.5F, angelOffset + 1.570796F, angel / 3.0F, angle, maxAngle, 1.0D);
                return;
            }
            if (!isLargeCave && localRandom.nextInt(4) == 0) {
                continue;
            }

            // Check if distance to working point (x and z) too larger than working radius (maybe ??)
            final double distanceX = x - realX;
            final double distanceZ = z - realZ;
            final double angelDiff = maxAngle - angle;
            final double newRadius = radius + 2.0F + 16.0F;
            if (distanceX * distanceX + distanceZ * distanceZ - angelDiff * angelDiff > newRadius * newRadius) {
                return;
            }

            //Boundaries check.
            if (x < realX - 16.0D - offsetXZ * 2.0D || z < realZ - 16.0D - offsetXZ * 2.0D || x > realX + 16.0D + offsetXZ * 2.0D || z > realZ + 16.0D + offsetXZ * 2.0D) {
                continue;
            }

            int xFrom = MathHelper.floor(x - offsetXZ) - chunkX * 16 - 1;
            int xTo = MathHelper.floor(x + offsetXZ) - chunkX * 16 + 1;

            int yFrom = MathHelper.floor(y - offsetY) - 1;
            int yTo = MathHelper.floor(y + offsetY) + 1;

            int zFrom = MathHelper.floor(z - offsetXZ) - chunkZ * 16 - 1;
            int zTo = MathHelper.floor(z + offsetXZ) - chunkZ * 16 + 1;

            if (xFrom < 0) {
                xFrom = 0;
            }
            if (xTo > 16) {
                xTo = 16;
            }

            if (yFrom < 1) {
                yFrom = 1;
            }
            if (yTo > this.worldHeightCap - 8) {
                yTo = this.worldHeightCap - 8;
            }
            if (zFrom < 0) {
                zFrom = 0;
            }
            if (zTo > 16) {
                zTo = 16;
            }

            // Search for water
            boolean waterFound = false;
            for (int xx = xFrom; !waterFound && xx < xTo; xx++) {
                for (int zz = zFrom; !waterFound && zz < zTo; zz++) {
                    for (int yy = yTo + 1; !waterFound && yy >= yFrom - 1; yy--) {
                        if (yy >= 0 && yy < this.worldHeightCap) {
                            final int block = chunk.getBlockId(xx, yy, zz);
                            if (block == BlockID.WATER || block == BlockID.STILL_WATER) {
                                waterFound = true;
                            }
                            if (yy != yFrom - 1 && xx != xFrom && xx != xTo - 1 && zz != zFrom && zz != zTo - 1) {
                                yy = yFrom;
                            }
                        }
                    }
                }
            }

            if (waterFound) {
                continue;
            }

            // Generate cave
            for (int xx = xFrom; xx < xTo; xx++) {
                final double modX = (xx + chunkX * 16 + 0.5D - x) / offsetXZ;
                for (int zz = zFrom; zz < zTo; zz++) {
                    final double modZ = (zz + chunkZ * 16 + 0.5D - z) / offsetXZ;

                    boolean grassFound = false;
                    if (modX * modX + modZ * modZ < 1.0D) {
                        for (int yy = yTo; yy > yFrom; yy--) {
                            final double modY = ((yy - 1) + 0.5D - y) / offsetY;
                            if (modY > -0.7D && modX * modX + modY * modY + modZ * modZ < 1.0D) {
                                final Biome biome = EnumBiome.getBiome(chunk.getBiomeId(xx, zz));
                                if (!(biome instanceof CoveredBiome)) {
                                    continue;
                                }

                                final int material = chunk.getBlockId(xx, yy, zz);
                                final int materialAbove = chunk.getBlockId(xx, yy + 1, zz);
                                if (material == BlockID.GRASS || material == BlockID.MYCELIUM) {
                                    grassFound = true;
                                }
                                //TODO: check this
//								if (this.isSuitableBlock(material, materialAbove, biome))
                                if (yy - 1 < 10) {
                                    chunk.setBlock(xx, yy, zz, BlockID.LAVA);
                                } else {
                                    chunk.setBlock(xx, yy, zz, BlockID.AIR);

                                    // If grass was just deleted, try to
                                    // move it down
                                    if (grassFound && chunk.getBlockId(xx, yy - 1, zz) == BlockID.DIRT) {
                                        chunk.setBlock(xx, yy - 1, zz, ((CoveredBiome) biome).getSurfaceBlock(yy - 1));
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

    protected void generateChunk(final int chunkX, final int chunkZ, final FullChunk generatingChunkBuffer) {
        int i = this.random.nextInt(this.random.nextInt(this.random.nextInt(PopulatorCaves.caveFrequency) + 1) + 1);
        if (PopulatorCaves.evenCaveDistribution) {
            i = PopulatorCaves.caveFrequency;
        }
        if (this.random.nextInt(100) >= PopulatorCaves.caveRarity) {
            i = 0;
        }

        for (int j = 0; j < i; j++) {
            final double x = chunkX * 16 + this.random.nextInt(16);

            final double y;

            if (PopulatorCaves.evenCaveDistribution) {
                y = PopulatorCaves.numberInRange(this.random, PopulatorCaves.caveMinAltitude, PopulatorCaves.caveMaxAltitude);
            } else {
                y = this.random.nextInt(this.random.nextInt(PopulatorCaves.caveMaxAltitude - PopulatorCaves.caveMinAltitude + 1) + 1) + PopulatorCaves.caveMinAltitude;
            }

            final double z = chunkZ * 16 + this.random.nextInt(16);

            int count = PopulatorCaves.caveSystemFrequency;
            boolean largeCaveSpawned = false;
            if (this.random.nextInt(100) <= PopulatorCaves.individualCaveRarity) {
                this.generateLargeCaveNode(this.random.nextLong(), generatingChunkBuffer, x, y, z);
                largeCaveSpawned = true;
            }

            if (largeCaveSpawned || this.random.nextInt(100) <= PopulatorCaves.caveSystemPocketChance - 1) {
                count += PopulatorCaves.numberInRange(this.random, PopulatorCaves.caveSystemPocketMinSize, PopulatorCaves.caveSystemPocketMaxSize);
            }
            while (count > 0) {
                count--;
                final float f1 = this.random.nextFloat() * 3.141593F * 2.0F;
                final float f2 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
                final float f3 = this.random.nextFloat() * 2.0F + this.random.nextFloat();

                this.generateCaveNode(this.random.nextLong(), generatingChunkBuffer, x, y, z, f3, f1, f2, 0, 0, 1.0D);
            }
        }
    }

}
