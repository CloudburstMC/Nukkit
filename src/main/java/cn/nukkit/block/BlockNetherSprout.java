package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.BlockColor;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNetherSprout extends BlockRoots {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockNetherSprout() {
    }

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
        return Item.EMPTY_ARRAY;
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
