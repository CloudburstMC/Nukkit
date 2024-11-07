package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockFenceCrimson extends BlockFence {

    public BlockFenceCrimson() {
        this(0);
    }

    public BlockFenceCrimson(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Fence";
    }

    @Override
    public int getId() {
        return CRIMSON_FENCE;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
