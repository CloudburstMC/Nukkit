package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLog2 extends BlockLog {

    public static final int ACACIA = 0;
    public static final int DARK_OAK = 1;

    public BlockLog2(Identifier id) {
        super(id);
    }

    @Override
    public Item toItem() {
        if ((getDamage() & 0b1100) == 0b1100) {
            return Item.get(BlockIds.WOOD, this.getDamage() & 0x3 + 4);
        } else {
            return Item.get(id, this.getDamage() & 0x03);
        }
    }
}
