package cn.nukkit.block;

import cn.nukkit.item.Item;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLeaves2 extends BlockLeaves {
    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public BlockLeaves2() {
        this(0);
    }

    public BlockLeaves2(int meta) {
        super(meta);
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
    protected boolean canDropApple() {
        return (this.getDamage() & 0x01) != 0;
    }

    @Override
    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, (this.getDamage() & 0x01) + 4);
    }
}
