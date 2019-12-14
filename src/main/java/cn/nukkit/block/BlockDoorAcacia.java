package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDoorAcacia;

public class BlockDoorAcacia extends BlockDoorWood {

    public BlockDoorAcacia(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Acacia Door Block";
    }

    @Override
    public Item toItem() {
        return new ItemDoorAcacia();
    }
}
