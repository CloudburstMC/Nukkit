package cn.nukkit.level.generator.object.ore;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.level.ChunkManager;
import cn.nukkit.math.MathHelper;
import cn.nukkit.math.NukkitRandom;

import static cn.nukkit.block.BlockID.STONE;

/**
 * @author MagicDroidX (Nukkit Project)
 */
//porktodo: rewrite this, the whole class is terrible and generated ores look stupid
public class OreType {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final OreType[] EMPTY_ARRAY = new OreType[0];
    
    @Deprecated
    @DeprecationDetails(reason = "The meta is limited to 32 bits", since = "1.3.0.0-PN")
    public final int fullId;
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN") public final int blockId;
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN") public final int blockData;
    
    public final int clusterCount;
    public final int clusterSize;
    public final int maxHeight;
    public final int minHeight;
    public final int replaceBlockId;

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight) {
        this(material, clusterCount, clusterSize, minHeight, maxHeight, STONE);
    }

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight, int replaceBlockId) {
        this.fullId = material.getFullId();
        this.blockId = material.getId();
        this.blockData = material.getDamage();
        this.clusterCount = clusterCount;
        this.clusterSize = clusterSize;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
        this.replaceBlockId = replaceBlockId;
    }

    public boolean spawn(ChunkManager level, NukkitRandom rand, int replaceId, int x, int y, int z) {
        float piScaled = rand.nextFloat() * (float) Math.PI;
        double scaleMaxX = (float) (x + 8) + MathHelper.sin(piScaled) * (float) clusterSize / 8.0F;
        double scaleMinX = (float) (x + 8) - MathHelper.sin(piScaled) * (float) clusterSize / 8.0F;
        double scaleMaxZ = (float) (z + 8) + MathHelper.cos(piScaled) * (float) clusterSize / 8.0F;
        double scaleMinZ = (float) (z + 8) - MathHelper.cos(piScaled) * (float) clusterSize / 8.0F;
        double scaleMaxY = y + rand.nextBoundedInt(3) - 2;
        double scaleMinY = y + rand.nextBoundedInt(3) - 2;

        for (int i = 0; i < clusterSize; ++i) {
            float sizeIncr = (float) i / (float) clusterSize;
            double scaleX = scaleMaxX + (scaleMinX - scaleMaxX) * (double) sizeIncr;
            double scaleY = scaleMaxY + (scaleMinY - scaleMaxY) * (double) sizeIncr;
            double scaleZ = scaleMaxZ + (scaleMinZ - scaleMaxZ) * (double) sizeIncr;
            double randSizeOffset = rand.nextDouble() * (double) clusterSize / 16.0D;
            double randVec1 = (double) (MathHelper.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            double randVec2 = (double) (MathHelper.sin((float) Math.PI * sizeIncr) + 1.0F) * randSizeOffset + 1.0D;
            int minX = MathHelper.floor(scaleX - randVec1 / 2.0D);
            int minY = MathHelper.floor(scaleY - randVec2 / 2.0D);
            int minZ = MathHelper.floor(scaleZ - randVec1 / 2.0D);
            int maxX = MathHelper.floor(scaleX + randVec1 / 2.0D);
            int maxY = MathHelper.floor(scaleY + randVec2 / 2.0D);
            int maxZ = MathHelper.floor(scaleZ + randVec1 / 2.0D);

            for (int xSeg = minX; xSeg <= maxX; ++xSeg) {
                double xVal = ((double) xSeg + 0.5D - scaleX) / (randVec1 / 2.0D);

                if (xVal * xVal < 1.0D) {
                    for (int ySeg = minY; ySeg <= maxY; ++ySeg) {
                        double yVal = ((double) ySeg + 0.5D - scaleY) / (randVec2 / 2.0D);

                        if (xVal * xVal + yVal * yVal < 1.0D) {
                            for (int zSeg = minZ; zSeg <= maxZ; ++zSeg) {
                                double zVal = ((double) zSeg + 0.5D - scaleZ) / (randVec1 / 2.0D);

                                if (xVal * xVal + yVal * yVal + zVal * zVal < 1.0D) {
                                    if (level.getBlockIdAt(xSeg, ySeg, zSeg) == replaceBlockId) {
                                        level.setBlockAt(xSeg, ySeg, zSeg, blockId, blockData);
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
