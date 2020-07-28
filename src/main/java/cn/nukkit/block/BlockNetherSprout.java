package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

public class BlockNetherSprout extends BlockRoots {
    
    @Override
    public int getId() {
        return NETHER_SPROUTS_BLOCK;
    }

    @Override
    public String getName() {
        return "Nether Sprouts Block";
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.NETHER_SPROUTS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{ toItem() };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CYAN_BLOCK_COLOR;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }
}
