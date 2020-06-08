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
    protected boolean canDropApple() {
        return (this.getDamage() & 0x01) != 0;
    }

    @Override
    public Item toItem() {
        return Item.get(BlockID.LEAVES2, this.getDamage() & 0x1);
    }

    @Override
    protected Item getSapling() {
        return Item.get(BlockID.SAPLING, (this.getDamage() & 0x01) + 4);
    }

    @Override
    public boolean isCheckDecay() {
        return (this.getDamage() & 0x02) != 0;
    }

    @Override
    public void setCheckDecay(boolean checkDecay) {
        if (checkDecay) {
            this.setDamage(this.getDamage() | 0x02);
        } else {
            this.setDamage(this.getDamage() & ~0x02);
        }
    }

    @Override
    public boolean isPersistent() {
        return (this.getDamage() & 0x04) != 0;
    }

    @Override
    public void setPersistent(boolean persistent) {
        if (persistent) {
            this.setDamage(this.getDamage() | 0x04);
        } else {
            this.setDamage(this.getDamage() & ~0x04);
        }
    }
}
