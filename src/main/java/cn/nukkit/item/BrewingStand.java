package cn.nukkit.item;


import cn.nukkit.block.Block;

public class BrewingStand extends Item {

    public BrewingStand(int meta) {
        this(meta, 1);
    }

    public BrewingStand(int meta, int count) {
        super(BREWING_STAND, 0, count, "Brewing Stand");
        this.block = Block.get(BREWING_STAND_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}