package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDoorAcacia;

public class BlockDoorAcacia extends BlockDoorWood {

    public BlockDoorAcacia() {
        this(0);
    }

    public BlockDoorAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Acacia Door BlockType";
    }

    @Override
    public int getId() {
        return ACACIA_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemDoorAcacia();
    }
}
