package cn.nukkit.item;

import cn.nukkit.block.Block;

public class AcaciaDoor extends Item {
    public AcaciaDoor() {
        this(0, 1);
    }

    public AcaciaDoor(Integer meta) {
        this(meta, 1);
    }

    public AcaciaDoor(Integer meta, int count) {
        super(ACACIA_DOOR, 0, count, "Acacia Door");
        this.block = Block.get(Item.ACACIA_DOOR_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
