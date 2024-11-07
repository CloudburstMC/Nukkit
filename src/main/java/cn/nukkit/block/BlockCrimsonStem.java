package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.utils.BlockColor;

public class BlockCrimsonStem extends BlockStem {

    public BlockCrimsonStem() {
        this(0);
    }

    public BlockCrimsonStem(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Crimson Stem";
    }

    @Override
    public int getId() {
        return CRIMSON_STEM;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_CRIMSON_STEM;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }

    @Override
    public int getBurnChance() {
        return 0;
    }

    @Override
    public int getBurnAbility() {
        return 0;
    }
}