package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class ItemSignDarkOak extends Item {

    public ItemSignDarkOak() {
        this(0, 1);
    }

    public ItemSignDarkOak(Integer meta) {
        this(meta, 1);
    }

    public ItemSignDarkOak(Integer meta, int count) {
        super(DARKOAK_SIGN, 0, count, "Dark Oak Sign");
        this.block = Block.get(DARK_OAK_STANDING_SIGN);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
