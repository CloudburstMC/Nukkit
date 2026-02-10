package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;


public class BlockCopperBulb extends BlockCopperBase {

    public BlockCopperBulb() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Copper Bulb";
    }

    @Override
    public int getId() {
        return COPPER_BULB;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }

    @Override
    protected int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return this.getId();
        }

        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed ? WAXED_COPPER_BULB : COPPER_BULB;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_COPPER_BULB : EXPOSED_COPPER_BULB;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_COPPER_BULB : WEATHERED_COPPER_BULB;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_COPPER_BULB : OXIDIZED_COPPER_BULB;
            default:
                return this.getId();
        }
    }
}
