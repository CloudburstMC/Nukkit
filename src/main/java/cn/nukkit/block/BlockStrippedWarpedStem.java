package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStrippedWarpedStem extends BlockStemStripped {

    public BlockStrippedWarpedStem() {
        this(0);
    }

    public BlockStrippedWarpedStem(int meta) {
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
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
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
