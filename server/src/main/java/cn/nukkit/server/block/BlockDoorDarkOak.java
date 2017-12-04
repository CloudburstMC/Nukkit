package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDoorDarkOak;

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
    public Item toItem() {
        return new ItemDoorDarkOak();
    }
}
