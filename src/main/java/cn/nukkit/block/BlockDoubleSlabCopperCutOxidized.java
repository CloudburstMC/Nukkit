package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutOxidized extends BlockDoubleSlabCopperCut {
    
    public BlockDoubleSlabCopperCutOxidized() {
        this(0);
    }

    public BlockDoubleSlabCopperCutOxidized(int meta) {
        super(meta);
    }
    
    @Override
    public int getSingleSlabId() {
        return OXIDIZED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }
}
