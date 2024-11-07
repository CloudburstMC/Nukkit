package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockDoorJungle extends BlockDoorWood {

    public BlockDoorJungle() {
        this(0);
    }

    public BlockDoorJungle(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Jungle Door Block";
    }

    @Override
    public int getId() {
        return JUNGLE_DOOR_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(Item.JUNGLE_DOOR);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
