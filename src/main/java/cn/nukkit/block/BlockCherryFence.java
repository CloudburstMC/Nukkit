package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockCherryFence extends BlockFence {

    public BlockCherryFence() {
        this(0);
    }

    public BlockCherryFence(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Cherry Fence";
    }

    @Override
    public int getId() {
        return CHERRY_FENCE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
