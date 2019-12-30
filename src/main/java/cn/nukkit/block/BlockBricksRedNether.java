package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.RED_NETHER_BRICK;

public class BlockBricksRedNether extends BlockNetherBrick {

    public BlockBricksRedNether(Identifier id) {
        super(id);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            return new Item[]{
                    Item.get(RED_NETHER_BRICK, 0, 1)
            };
        } else {
            return new Item[0];
        }
    }

}
