package cn.nukkit.item;

import cn.nukkit.block.Block;

/**
 * Created by Snake1999 on 2016/2/3.
 * Package cn.nukkit.item in project Nukkit.
 */
public class ItemSkull extends Item {

    public ItemSkull() {
        this(0, 1);
    }

    public ItemSkull(Integer meta) {
        this(meta, 1);
    }

    public ItemSkull(Integer meta, int count) {
        super(SKULL, meta, count, "Skull");
        this.block = Block.get(Block.SKULL_BLOCK);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
