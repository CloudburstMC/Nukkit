package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockDoubleSlabCopperCutExposed extends BlockDoubleSlabCopperCut {
    
    public BlockDoubleSlabCopperCutExposed() {
        this(0);
    }
    
    public BlockDoubleSlabCopperCutExposed(int meta) {
        super(meta);
    }

    @Override
    public int getSingleSlabId() {
        return EXPOSED_CUT_COPPER_SLAB;
    }

    @Override
    public int getId() {
        return EXPOSED_DOUBLE_CUT_COPPER_SLAB;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
