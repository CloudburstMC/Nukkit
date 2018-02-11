package cn.nukkit.level.generator.object.ore;

import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OreType {
    public final int fullId;
    public final int clusterCount;
    public final int clusterSize;
    public final int maxHeight;
    public final int minHeight;

    private final double maxSizeO8;
    private final double maxSizeO16;
    private final double sizeInverse;

    private static final double ONE_2 = 1 / 2F;
    private static final double ONE_8 = 1 / 8F;
    private static final double ONE_16 = 1 / 16F;

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight) {
        this.fullId = material.getFullId();
        this.clusterCount = clusterCount;
        this.clusterSize = clusterSize;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;

        this.maxSizeO8 = clusterSize * ONE_8;
        this.maxSizeO16 = clusterSize * ONE_16;
        this.sizeInverse = 1.0 / clusterSize;
    }

    public boolean spawn(ChunkManager level, NukkitRandom rand, int replaceId, int x, int y, int z) {
        double f = rand.nextDouble() * Math.PI;

        int x8 = x + 8;
        int z8 = z + 8;
        double so8 = maxSizeO8;
        double so16 = maxSizeO16;
        double sf = MathHelper.sin(f) * so8;
        double cf = MathHelper.cos(f) * so8;
        double d1 = x8 + sf;
        double d2 = x8 - sf;
        double d3 = z8 + cf;
        double d4 = z8 - cf;

        double d5 = y + rand.nextRange(0, 3) - 2;
        double d6 = y + rand.nextRange(0, 3) - 2;

        double xd = (d2 - d1);
        double yd = (d6 - d5);
        double zd = (d4 - d3);

        double iFactor = 0;
        for (int i = 0; i < clusterSize; i++, iFactor += sizeInverse) {
            double d7 = d1 + xd * iFactor;
            double d8 = d5 + yd * iFactor;
            double d9 = d3 + zd * iFactor;

            double d10 = rand.nextDouble() * so16;
            double sif = MathHelper.sin(Math.PI * iFactor);
            double d11 = (sif + 1.0) * d10 + 1.0;
            double d12 = (sif + 1.0) * d10 + 1.0;

            double d11o2 = d11 * ONE_2;
            double d12o2 = d12 * ONE_2;

            int minX = MathHelper.floor(d7 - d11o2);
            int minY = Math.max(1, MathHelper.floor(d8 - d12o2));
            int minZ = MathHelper.floor(d9 - d11o2);

            int maxX = MathHelper.floor(d7 + d11o2);
            int maxY = Math.min(255, MathHelper.floor(d8 + d12o2));
            int maxZ = MathHelper.floor(d9 + d11o2);

            double id11o2 = 1.0 / (d11o2);
            double id12o2 = 1.0 / (d12o2);

            for (int xx = minX; xx <= maxX; xx++) {
                double dx = (xx + 0.5D - d7) * id11o2;
                double dx2 = dx * dx;
                if (dx2 < 1) {
                    for (int yy = minY; yy <= maxY; yy++) {
                        double dy = (yy + 0.5D - d8) * id12o2;
                        double dxy2 = dx2 + dy * dy;
                        if (dxy2 < 1) {
                            for (int zz = minZ; zz <= maxZ; zz++) {
                                double dz = (zz + 0.5D - d9) * id11o2;
                                double dxyz2 = dxy2 + dz * dz;
                                if ((dxyz2 < 1)) {
                                    if (level.getBlockIdAt(xx, yy, zz) == replaceId) {
                                        level.setBlockFullIdAt(xx, yy, zz, fullId);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}
