package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockStairsSmoothSandstone extends BlockStairsSandstone {

    public BlockStairsSmoothSandstone() {
        this(0);
    }

    public BlockStairsSmoothSandstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Smooth Sandstone Stairs";
    }

    @Override
    public int getId() {
        return SMOOTH_SANDSTONE_STAIRS;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
