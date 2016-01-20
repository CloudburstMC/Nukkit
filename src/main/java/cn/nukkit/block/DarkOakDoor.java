package cn.nukkit.block;

import cn.nukkit.item.Item;

public class DarkOakDoor extends WoodDoor {

    public DarkOakDoor() {
        this(0);
    }

    public DarkOakDoor(int meta) {
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
                {Item.ACACIA_DOOR, 0, 1}
        };
    }
}
