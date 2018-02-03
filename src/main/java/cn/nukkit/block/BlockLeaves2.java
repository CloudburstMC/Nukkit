package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemApple;
import cn.nukkit.item.ItemBlock;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLeaves2 extends BlockLeaves {

    public BlockLeaves2() {
        this(0);
    }

    public BlockLeaves2(int meta) {
        super(meta);
    }

    public String getName() {
        String[] names = new String[]{
                "Oak Leaves",
                "Spruce Leaves",
                "Birch Leaves",
                "Jungle Leaves"
        };
        return names[this.getDamage() & 0x01];
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            if ((int) ((Math.random()) * 200) == 0 && this.getDamage() == DARK_OAK) {
                return new Item[]{
                        new ItemApple()
                };
            }
            if ((int) ((Math.random()) * 20) == 0) {
                return new Item[]{
                        new ItemBlock(new BlockSapling(), this.getDamage() & 0x03, 1)
                };
            }
        }
        return new Item[0];
    }
}
