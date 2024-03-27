package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

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
        return Item.get(Item.SPRUCE_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
