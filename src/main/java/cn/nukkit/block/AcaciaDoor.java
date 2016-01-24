package cn.nukkit.block;

import cn.nukkit.item.Item;

public class AcaciaDoor extends WoodDoor {

    public AcaciaDoor() {
        this(0);
    }

    public AcaciaDoor(int meta) {
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
