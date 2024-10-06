package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperCutOxidized extends BlockCopperCut {

    public BlockCopperCutOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Oxidized Cut Copper";
    }

    @Override
    public int getId() {
        return OXIDIZED_CUT_COPPER;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_NYLIUM_BLOCK_COLOR;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.OXIDIZED;
    }
}
