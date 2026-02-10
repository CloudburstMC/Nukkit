package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledWeathered extends BlockCopperChiseled {

    public BlockCopperChiseledWeathered() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Weathered Chiseled Copper";
    }

    @Override
    public int getId() {
        return WEATHERED_CHISELED_COPPER;
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
