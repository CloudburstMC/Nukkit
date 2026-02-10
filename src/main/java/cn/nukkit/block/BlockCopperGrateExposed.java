package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperGrateExposed extends BlockCopperGrate {

    public BlockCopperGrateExposed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Exposed Copper Grate";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_GRATE;
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
