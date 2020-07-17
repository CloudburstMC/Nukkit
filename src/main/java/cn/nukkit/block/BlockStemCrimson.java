package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

public class BlockStemCrimson extends BlockStem {

    public BlockStemCrimson() {
        this(0);
    }

    public BlockStemCrimson(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CRIMSON_STEM;
    }

    @Override
    public String getName() {
        return "Crimson Stem";
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_CRIMSON_STEM);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }

}
