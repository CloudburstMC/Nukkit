package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

public class BlockStemWarped extends BlockStem {

    public BlockStemWarped() {
        this(0);
    }

    public BlockStemWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_STEM;
    }

    @Override
    public String getName() {
        return "Warped Stem";
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_WARPED_STEM);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}
