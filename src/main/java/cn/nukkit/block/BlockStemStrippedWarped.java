package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStemStrippedWarped extends BlockStemStripped {
    public BlockStemStrippedWarped() {
        this(0);
    }
    
    public BlockStemStrippedWarped(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_WARPED_STEM;
    }
    
    @Override
    public String getName() {
        return "Stripped Warped Stem";
    }
    
    @Override
    public int getBurnChance() {
        return 0;
    }
    
    @Override
    public int getBurnAbility() {
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}
