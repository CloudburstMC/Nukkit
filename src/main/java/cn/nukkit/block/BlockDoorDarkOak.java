package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockDoorDarkOak extends BlockDoorWood {

    public BlockDoorDarkOak() {
        this(0);
    }

    public BlockDoorDarkOak(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Dark Oak Door Block";
    }

    @Override
    public int getId() {
        return DARK_OAK_DOOR_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.DARK_OAK_DOOR, 0, 1}
        };
    }
}
