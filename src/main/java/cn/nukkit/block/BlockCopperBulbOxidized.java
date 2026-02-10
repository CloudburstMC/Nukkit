package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperBulbOxidized extends BlockCopperBulb {

    public BlockCopperBulbOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Oxidized Copper Bulb";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_BULB;
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
