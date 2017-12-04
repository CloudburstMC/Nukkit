package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemDoorSpruce;

public class BlockDoorSpruce extends BlockDoorWood {

    public BlockDoorSpruce() {
        this(0);
    }

    public BlockDoorSpruce(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Door Block";
    }

    @Override
    public int getId() {
        return SPRUCE_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemDoorSpruce();
    }
}
