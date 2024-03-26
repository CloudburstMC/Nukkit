package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockInfoUpdate extends BlockSolid {

    @Override
    public int getId() {
        return INFO_UPDATE;
    }

    @Override
    public String getName() {
        return "Update Game Block";
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[0];
    }
}
