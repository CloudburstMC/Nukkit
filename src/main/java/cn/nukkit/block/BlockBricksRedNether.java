package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;

public class BlockBricksRedNether extends BlockNetherBrick {

    @Override
    public String getName() {
        return "Red Nether Bricks";
    }

    @Override
    public int getId() {
        return RED_NETHER_BRICK;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(Item.RED_NETHER_BRICK, 0, 1)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
