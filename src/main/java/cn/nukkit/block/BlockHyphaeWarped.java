package cn.nukkit.block;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.utils.BlockColor;

public class BlockHyphaeWarped extends BlockStem {
    public BlockHyphaeWarped() {
        this(0);
    }
    
    public BlockHyphaeWarped(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return WARPED_HYPHAE;
    }

    @Override
    public String getName() {
        return "Warped Hyphae";
    }

    @Override
    protected BlockState getStrippedState() {
        return getCurrentState().withBlockId(STRIPPED_WARPED_HYPHAE);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_HYPHAE_BLOCK_COLOR;
    }
}
