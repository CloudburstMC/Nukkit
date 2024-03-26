package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockWarpedTrapdoor extends BlockTrapdoor {

    public BlockWarpedTrapdoor() {
        this(0);
    }

    public BlockWarpedTrapdoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Trapdoor";
    }

    @Override
    public int getId() {
        return WARPED_TRAPDOOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
