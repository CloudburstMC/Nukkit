package cn.nukkit.server.item;

import cn.nukkit.server.block.BlockDoorAcacia;

public class ItemDoorAcacia extends Item {
    public ItemDoorAcacia() {
        this(0, 1);
    }

    public ItemDoorAcacia(Integer meta) {
        this(meta, 1);
    }

    public ItemDoorAcacia(Integer meta, int count) {
        super(ACACIA_DOOR, 0, count, "Acacia Door");
        this.block = new BlockDoorAcacia();
    }

}
