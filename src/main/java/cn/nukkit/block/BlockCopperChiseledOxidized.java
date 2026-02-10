package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperChiseledOxidized extends BlockCopperChiseled {

    public BlockCopperChiseledOxidized() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Oxidized Chiseled Copper";
    }

    @Override
    public int getId() {
        return OXIDIZED_CHISELED_COPPER;
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
