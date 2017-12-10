package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
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
        return "Beetroot BlockType";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (this.meta >= 0x07) {
            return new Item[]{
                    Item.get(Item.BEETROOT, 0, 1),
                    Item.get(Item.BEETROOT_SEEDS, 0, (int) (4d * Math.random()))
            };
        } else {
            return new Item[]{
                    Item.get(Item.BEETROOT_SEEDS, 0, 1)
            };
        }
    }
}
