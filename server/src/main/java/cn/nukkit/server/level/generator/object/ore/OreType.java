package cn.nukkit.server.level.generator.object.ore;

import cn.nukkit.server.block.Block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class OreType {
    public final Block material;
    public final int clusterCount;
    public final int clusterSize;
    public final int maxHeight;
    public final int minHeight;

    public OreType(Block material, int clusterCount, int clusterSize, int minHeight, int maxHeight) {
        this.material = material;
        this.clusterCount = clusterCount;
        this.clusterSize = clusterSize;
        this.maxHeight = maxHeight;
        this.minHeight = minHeight;
    }
}
