package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperGrateWeathered extends BlockCopperGrate {

    public BlockCopperGrateWeathered() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Weathered Copper Grate";
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_GRATE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WARPED_STEM_BLOCK_COLOR;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.WEATHERED;
    }
}
