package cn.nukkit.utils;

import cn.nukkit.math.BlockFace;

/**
 * Interface of a faceable Block
 */
public interface Faceable {

    /**
     * Get BlockFace of the direction the block is facing
     *
     * @return BlockFace of the direction the block is facing
     */
    BlockFace getBlockFace();
}