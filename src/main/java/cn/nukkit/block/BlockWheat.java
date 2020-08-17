package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSeedsWheat;
import cn.nukkit.item.ItemWheat;

/**
 * @author xtypr
 * @since 2015/12/2
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
    public Item toItem() {
        return new ItemSeedsWheat();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() >= 0x07) {
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
