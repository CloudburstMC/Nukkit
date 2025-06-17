package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockBambooFence extends BlockFence {

    public BlockBambooFence() {
        this(0);
    }

    public BlockBambooFence(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Bamboo Fence";
    }

    @Override
    public int getId() {
        return BAMBOO_FENCE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.YELLOW_BLOCK_COLOR;
    }
}
