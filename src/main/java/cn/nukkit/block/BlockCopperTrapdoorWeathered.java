package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperTrapdoorWeathered extends BlockCopperTrapdoor {

    public BlockCopperTrapdoorWeathered() {
        this(0);
    }

    public BlockCopperTrapdoorWeathered(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Weathered Copper Trapdoor";
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_TRAPDOOR;
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
