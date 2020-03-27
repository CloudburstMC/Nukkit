package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.SAPLING;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLeaves2 extends BlockLeaves {
    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public BlockLeaves2(Identifier id) {
        super(id);
    }

    @Override
    protected boolean canDropApple() {
        return (this.getMeta() & 0x01) != 0;
    }

    @Override
    protected Item getSapling() {
        return Item.get(SAPLING, (this.getMeta() & 0x01) + 4);
    }
}
