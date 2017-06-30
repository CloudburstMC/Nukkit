package cn.nukkit.item;

import cn.nukkit.block.BlockBrewingStand;

public class ItemBrewingStand extends Item {

    public ItemBrewingStand() {
        this(0, 1);
    }

    public ItemBrewingStand(Integer meta) {
        this(meta, 1);
    }

    public ItemBrewingStand(Integer meta, int count) {
        super(BREWING_STAND, 0, count, "Brewing Stand");
        this.block = new BlockBrewingStand();
    }

}