package cn.nukkit.redstone;

import cn.nukkit.block.Block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class UpdateObject {

    private final int population;
    private Block location = null;

    public UpdateObject(int blockPopulation, Block blockLocation) {
        population = blockPopulation;
        location = blockLocation;
    }

    public int getPopulation() {
        return population;
    }

    public Block getLocation() {
        return location;
    }

}
