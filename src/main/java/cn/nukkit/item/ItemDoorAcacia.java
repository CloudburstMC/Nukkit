package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;

public class ItemDoorAcacia extends Item {
    public ItemDoorAcacia() {
        this(0, 1);
    }

    public ItemDoorAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorAcacia(Integer meta, int count) {
        super(ACACIA_DOOR, 0, count, "Acacia Door");
        this.block = Block.get(BlockID.ACACIA_DOOR_BLOCK);
    }

}
