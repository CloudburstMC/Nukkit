package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.item.ItemIds.BEETROOT_SEEDS;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockBeetroot extends BlockCrops {
    public BlockBeetroot(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.BEETROOT_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(ItemIds.BEETROOT, 0, 1),
                    Item.get(BEETROOT_SEEDS, 0, (int) (4d * Math.random()))
            };
        } else {
            return new Item[]{
                    Item.get(BEETROOT_SEEDS, 0, 1)
            };
        }
    }
}
