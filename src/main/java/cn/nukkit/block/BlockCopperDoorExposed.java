package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperDoorExposed extends BlockCopperDoor {

    public BlockCopperDoorExposed() {
        this(0);
    }

    public BlockCopperDoorExposed(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Exposed Copper Door";
    }

    @Override
    public int getId() {
        return EXPOSED_COPPER_DOOR_BLOCK;
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
