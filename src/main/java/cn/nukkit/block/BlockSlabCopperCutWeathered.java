package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutWeathered extends BlockSlabCopperCut {
    
    public BlockSlabCopperCutWeathered() {
        this(0);
    }
    
    public BlockSlabCopperCutWeathered(int meta) {
        super(meta, WEATHERED_DOUBLE_CUT_COPPER_SLAB);
    }
    
    protected BlockSlabCopperCutWeathered(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return WEATHERED_CUT_COPPER_SLAB;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }
}
