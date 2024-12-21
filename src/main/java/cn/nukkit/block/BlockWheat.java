package cn.nukkit.block;

import cn.nukkit.item.Item;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
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
        return Item.get(Item.WHEAT_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.WHEAT),
                    Item.get(Item.WHEAT_SEEDS, 0, ThreadLocalRandom.current().nextInt(0, 4))
            };
        } else {
            return new Item[]{
                    Item.get(Item.WHEAT_SEEDS)
            };
        }
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
