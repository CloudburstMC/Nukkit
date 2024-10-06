package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWarpedStem extends BlockStem {

    public BlockWarpedStem() {
        this(0);
    }

    public BlockWarpedStem(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Warped Stem";
    }

    @Override
    public int getId() {
        return WARPED_STEM;
    }

    @Override
    public int getStrippedId() {
        return STRIPPED_WARPED_STEM;
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
