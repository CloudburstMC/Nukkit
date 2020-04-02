package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemBrewingStand extends Item {

    public ItemBrewingStand() {
        this(0, 1);
    }

    public ItemBrewingStand(Integer meta) {
        this(meta, 1);
    }

    public ItemBrewingStand(Integer meta, int count) {
        super(BREWING_STAND, 0, count, "Brewing Stand");
        this.block = Block.get(BlockID.BREWING_STAND_BLOCK);
    }

}