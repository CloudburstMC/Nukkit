package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperBulbWeathered extends BlockCopperBulb {

    public BlockCopperBulbWeathered() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Weathered Copper Bulb";
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_BULB;
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
