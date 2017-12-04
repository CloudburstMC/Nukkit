package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemSeedsWheat;
import cn.nukkit.server.item.ItemWheat;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
 */
public class BlockWheat extends BlockCrops {

    public BlockWheat() {
        this(0);
    }

    public BlockWheat(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Wheat Block";
    }

    @Override
    public int getId() {
        return WHEAT_BLOCK;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.meta >= 0x07) {
            return new Item[]{
                    new ItemWheat(),
                    new ItemSeedsWheat(0, (int) (4d * Math.random()))
            };
        } else {
            return new Item[]{
                    new ItemSeedsWheat()
            };
        }
    }
}
