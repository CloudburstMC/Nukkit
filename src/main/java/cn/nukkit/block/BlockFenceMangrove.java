package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockFenceMangrove extends BlockFence {

    public BlockFenceMangrove() {
        this(0);
    }

    public BlockFenceMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Fence";
    }

    @Override
    public int getId() {
        return MANGROVE_FENCE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
