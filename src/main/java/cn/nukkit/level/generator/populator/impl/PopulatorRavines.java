package cn.nukkit.level.generator.populator.impl;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.level.generator.populator.type.Populator;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

import java.util.Random;

public class PopulatorRavines extends Populator {

    protected int checkAreaSize = 8;

    private Random random;
    private long worldLong1;
    private long worldLong2;

    private int ravineRarity = 1;//2
    private int ravineMinAltitude = 20;
    private int ravineMaxAltitude = 67;
    private int ravineMinLength = 84;
    private int ravineMaxLength = 111;

    private double ravineDepth = 3;

    private int worldHeightCap = 1 << 8;

    private float[] a = new float[1024];

    @Override
    public void populate(ChunkManager level, int chunkX, int chunkZ, NukkitRandom random, Chunk chunk) {
        this.random = new Random();
        this.random.setSeed(level.getSeed());
        worldLong1 = this.random.nextLong();
        worldLong2 = this.random.nextLong();

        int i = this.checkAreaSize;

        for (int x = chunkX - i; x <= chunkX + i; x++)
            for (int z = chunkZ - i; z <= chunkZ + i; z++) {
                long l3 = x * worldLong1;
                long l4 = z * worldLong2;
                this.random.setSeed(l3 ^ l4 ^ level.getSeed());
                generateChunk(chunkX, chunkZ, level.getChunk(chunkX, chunkZ));
            }
    }

    protected void generateChunk(int chunkX, int chunkZ, Chunk generatingChunkBuffer) {
        if (this.random.nextInt(300) >= this.ravineRarity)
            return;
        double d1 = (chunkX * 16) + this.random.nextInt(16);
        double d2 = numberInRange(random, this.ravineMinAltitude, this.ravineMaxAltitude);
        double d3 = (chunkZ * 16) + this.random.nextInt(16);

        int i = 1;

        for (int j = 0; j < i; j++) {
            float f1 = this.random.nextFloat() * 3.141593F * 2.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float f3 = (this.random.nextFloat() * 2.0F + this.random.nextFloat()) * 2.0F;

            int size = numberInRange(random, this.ravineMinLength, this.ravineMaxLength);

            createRavine(this.random.nextLong(), generatingChunkBuffer, d1, d2, d3, f3, f1, f2, size, this.ravineDepth);
        }
    }

    protected void createRavine(long paramLong, Chunk generatingChunkBuffer, double paramDouble1, double paramDouble2, double paramDouble3,
                                float paramFloat1, float paramFloat2, float paramFloat3, int size, double paramDouble4) {
        Random localRandom = new Random(paramLong);

        int chunkX = generatingChunkBuffer.getX();
        int chunkZ = generatingChunkBuffer.getZ();

        double d1 = chunkX * 16 + 8;
        double d2 = chunkZ * 16 + 8;

        float f1 = 0.0F;
        float f2 = 0.0F;

        int i = 0;

        float f3 = 1.0F;
        for (int j = 0; ; j++) {
            if (j >= worldHeightCap)
                break;
            if ((j == 0) || (localRandom.nextInt(3) == 0)) {
                f3 = 1.0F + localRandom.nextFloat() * localRandom.nextFloat() * 1.0F;
            }
            this.a[j] = (f3 * f3);
        }

        for (int stepCount = 0; stepCount < size; stepCount++) {
            double d3 = 1.5D + MathHelper.sin(stepCount * 3.141593F / size) * paramFloat1 * 1.0F;
            double d4 = d3 * paramDouble4;

            d3 *= (localRandom.nextFloat() * 0.25D + 0.75D);
            d4 *= (localRandom.nextFloat() * 0.25D + 0.75D);

            float f4 = MathHelper.cos(paramFloat3);
            float f5 = MathHelper.sin(paramFloat3);
            paramDouble1 += MathHelper.cos(paramFloat2) * f4;
            paramDouble2 += f5;
            paramDouble3 += MathHelper.sin(paramFloat2) * f4;

            paramFloat3 *= 0.7F;

            paramFloat3 += f2 * 0.05F;
            paramFloat2 += f1 * 0.05F;

            f2 *= 0.8F;
            f1 *= 0.5F;
            f2 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 2.0F;
            f1 += (localRandom.nextFloat() - localRandom.nextFloat()) * localRandom.nextFloat() * 4.0F;

            if ((i == 0) && (localRandom.nextInt(4) == 0)) {
                continue;
            }
            double d5 = paramDouble1 - d1;
            double d6 = paramDouble3 - d2;
            double d7 = size - stepCount;
            double d8 = paramFloat1 + 2.0F + 16.0F;
            if (d5 * d5 + d6 * d6 - d7 * d7 > d8 * d8) {
                return;
            }

            if ((paramDouble1 < d1 - 16.0D - d3 * 2.0D) || (paramDouble3 < d2 - 16.0D - d3 * 2.0D) || (paramDouble1 > d1 + 16.0D + d3 * 2.0D) || (paramDouble3 > d2 + 16.0D + d3 * 2.0D))
                continue;
            int k = MathHelper.floor(paramDouble1 - d3) - (chunkX * 16) - 1;
            int m = MathHelper.floor(paramDouble1 + d3) - (chunkZ * 16) + 1;

            int maxY = MathHelper.floor(paramDouble2 - d4) - 1;
            int minY = MathHelper.floor(paramDouble2 + d4) + 1;

            int i2 = MathHelper.floor(paramDouble3 - d3) - (chunkX * 16) - 1;
            int i3 = MathHelper.floor(paramDouble3 + d3) - (chunkZ * 16) + 1;

            if (k < 0)
                k = 0;
            if (m > 16)
                m = 16;

            if (maxY < 1)
                maxY = 1;
            if (minY > this.worldHeightCap - 8)
                minY = this.worldHeightCap - 8;

            if (i2 < 0)
                i2 = 0;
            if (i3 > 16)
                i3 = 16;

            int i4 = 0;
            for (int localX = k; (i4 == 0) && (localX < m); localX++) {
                for (int localZ = i2; (i4 == 0) && (localZ < i3); localZ++) {
                    for (int localY = minY + 1; (i4 == 0) && (localY >= maxY - 1); localY--) {
                        if (localY < 0)
                            continue;
                        if (localY < this.worldHeightCap) {
                            int materialAtPosition = generatingChunkBuffer.getBlockId(localX, localY, localZ);
                            if (materialAtPosition == Block.WATER
                                    || materialAtPosition == Block.STILL_WATER) {
                                i4 = 1;
                            }
                            if ((localY != maxY - 1) && (localX != k) && (localX != m - 1) && (localZ != i2) && (localZ != i3 - 1))
                                localY = maxY;
                        }
                    }
                }
            }
            if (i4 != 0) {
                continue;
            }
            for (int localX = k; localX < m; localX++) {
                double d9 = (localX + (chunkX * 16) + 0.5D - paramDouble1) / d3;
                for (int localZ = i2; localZ < i3; localZ++) {
                    double d10 = (localZ + (chunkZ * 16) + 0.5D - paramDouble3) / d3;
                    if (d9 * d9 + d10 * d10 < 1.0D) {
                        for (int localY = minY; localY >= maxY; localY--) {
                            double d11 = ((localY - 1) + 0.5D - paramDouble2) / d4;
                            if ((d9 * d9 + d10 * d10) * this.a[localY - 1] + d11 * d11 / 6.0D < 1.0D) {
                                int material = generatingChunkBuffer.getBlockId(localX, localY, localZ);
                                if (material == Block.GRASS) {
                                    if (localY - 1 < 10) {
                                        generatingChunkBuffer.setBlockId(localX, localY, localZ, Block.LAVA);
                                    } else {
                                        generatingChunkBuffer.setBlockId(localX, localY, localZ, Block.AIR);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (i != 0)
                break;
        }
    }

    public static int numberInRange(Random random, int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}