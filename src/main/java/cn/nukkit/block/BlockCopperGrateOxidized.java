package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperGrateOxidized extends BlockCopperGrate {

    public BlockCopperGrateOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Oxidized Copper Grate";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_GRATE;
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
