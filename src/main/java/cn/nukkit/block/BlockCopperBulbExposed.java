package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperBulbExposed extends BlockCopperBulb {

    public BlockCopperBulbExposed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Exposed Copper Bulb";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_BULB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.EXPOSED;
    }
}
