package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopper extends BlockCopperBase {

    public BlockCopper() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Block of Copper";
    }

    @Override
    public int getId() {
        return COPPER_BLOCK;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
