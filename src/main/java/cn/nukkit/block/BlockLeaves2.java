package cn.nukkit.block;

import cn.nukkit.item.Item;
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
        super(meta & 0x7); // Anything above this range is invalid
    }

    public String getName() {
        String[] names = new String[]{
                "Acacia Leaves",
                "Dark Oak Leaves"
        };
        return names[this.getDamage() & 0x01];
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    protected boolean canDropApple() {
        return (this.getDamage() & 0x03) == DARK_OAK;
    }

    @Override
    protected Item getSapling() {
        return new ItemBlock(get(SAPLING), (this.getDamage() & 0x03) + 4);
    }
}
