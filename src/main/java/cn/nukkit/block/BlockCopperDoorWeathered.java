package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperDoorWeathered extends BlockCopperDoor {

    public BlockCopperDoorWeathered() {
        this(0);
    }

    public BlockCopperDoorWeathered(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Weathered Copper Door";
    }

    @Override
    public int getId() {
        return WEATHERED_COPPER_DOOR_BLOCK;
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
