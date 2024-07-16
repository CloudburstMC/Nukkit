package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Utils;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockBeetroot extends BlockCrops {
    public BlockBeetroot() {
        this(0);
    }

    public BlockBeetroot(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BEETROOT_BLOCK;
    }

    @Override
    public String getName() {
        return "Beetroot Block";
    }

    @Override
    public Item toItem() {
        return Item.get(Item.BEETROOT_SEEDS);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.getDamage() >= 0x07) {
            return new Item[]{
                    Item.get(Item.BEETROOT, 0, 1),
                    Item.get(Item.BEETROOT_SEEDS, 0, Utils.random.nextInt(0, 4))
            };
        } else {
            return new Item[]{
                    Item.get(Item.BEETROOT_SEEDS, 0, 1)
            };
        }
    }
}
