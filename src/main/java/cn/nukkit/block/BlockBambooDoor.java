package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockBambooDoor extends BlockDoorWood {

    public BlockBambooDoor() {
        this(0);
    }

    public BlockBambooDoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Door Block";
    }

    @Override
    public int getId() {
        return BAMBOO_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.BAMBOO_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
