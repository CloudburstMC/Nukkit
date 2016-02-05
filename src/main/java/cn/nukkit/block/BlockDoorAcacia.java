package cn.nukkit.block;

import cn.nukkit.item.Item;

public class BlockDoorAcacia extends BlockDoorWood {

    public BlockDoorAcacia() {
        this(0);
    }

    public BlockDoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }

    @Override
    public int getId() {
        return ACACIA_DOOR_BLOCK;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.ACACIA_DOOR, 0, 1}
        };
    }
}
