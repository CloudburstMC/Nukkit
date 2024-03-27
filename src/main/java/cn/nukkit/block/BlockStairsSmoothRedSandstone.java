package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;

public class BlockStairsSmoothRedSandstone extends BlockStairsRedSandstone {

    public BlockStairsSmoothRedSandstone() {
        this(0);
    }

    public BlockStairsSmoothRedSandstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Smooth RedSand stone Stairs";
    }

    @Override
    public int getId() {
        return SMOOTH_RED_SANDSTONE_STAIRS;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }
}
