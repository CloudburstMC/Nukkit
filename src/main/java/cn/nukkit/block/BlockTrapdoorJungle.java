package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorJungle extends BlockTrapdoor {

    public BlockTrapdoorJungle() {
        this(0);
    }

    public BlockTrapdoorJungle(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
