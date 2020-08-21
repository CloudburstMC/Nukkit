package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStemStrippedCrimson extends BlockStemStripped {
    public BlockStemStrippedCrimson() {
        this(0);
    }
    
    public BlockStemStrippedCrimson(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_CRIMSON_STEM;
    }
    
    @Override
    public String getName() {
        return "Stripped Crimson Stem";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.CRIMSON_STEM_BLOCK_COLOR;
    }
}
