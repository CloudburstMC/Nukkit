package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockSlabCopperCutExposed extends BlockSlabCopperCut {
    
    public BlockSlabCopperCutExposed() {
        this(0);
    }

    public BlockSlabCopperCutExposed(int meta) {
        super(meta, EXPOSED_DOUBLE_CUT_COPPER_SLAB);
    }
    
    protected BlockSlabCopperCutExposed(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return EXPOSED_CUT_COPPER_SLAB;
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
