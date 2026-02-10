package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledExposed extends BlockCopperChiseled {

    public BlockCopperChiseledExposed() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Exposed Chiseled Copper";
    }

    @Override
    public int getId() {
        return EXPOSED_CHISELED_COPPER;
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
