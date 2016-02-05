package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockDoorJungle extends BlockDoorWood {

    public BlockDoorJungle() {
        this(0);
    }

    public BlockDoorJungle(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Jungle Door Block";
    }

    @Override
    public int getId() {
        return JUNGLE_DOOR_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.JUNGLE_DOOR, 0, 1}
        };
    }
}
