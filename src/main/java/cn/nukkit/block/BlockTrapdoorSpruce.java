package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorSpruce extends BlockTrapdoor {

    public BlockTrapdoorSpruce() {
        this(0);
    }

    public BlockTrapdoorSpruce(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }

    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
