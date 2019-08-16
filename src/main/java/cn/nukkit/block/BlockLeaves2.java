package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

import java.util.concurrent.ThreadLocalRandom;

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
                "Acacia Leaves",
                "Dark Oak Leaves",
                "",
                ""
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
            if (ThreadLocalRandom.current().nextInt(200) == 0 && (this.getDamage() & 0x03) == DARK_OAK) {
                return new Item[]{
                        Item.get(Item.APPLE)
                };
            }
            if (ThreadLocalRandom.current().nextInt(20) == 0) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return new Item[]{
                            Item.get(Item.STICK, 0, ThreadLocalRandom.current().nextInt(1, 2))
                    };
                } else {
                    return new Item[]{
                            new ItemBlock(get(SAPLING), (this.getDamage() & 0x03) + 4)
                    };
                }
            }
        }
        return new Item[0];
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
