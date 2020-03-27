package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.utils.Identifier;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockWheat extends BlockCrops {

    public BlockWheat(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        return Item.get(ItemIds.WHEAT);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getMeta() >= 0x07) {
            return new Item[]{
                    Item.get(ItemIds.WHEAT),
                    Item.get(ItemIds.WHEAT, 0, (int) (4f * Math.random()))
            };
        } else {
            return new Item[]{
                    Item.get(ItemIds.WHEAT)
            };
        }
    }
}
