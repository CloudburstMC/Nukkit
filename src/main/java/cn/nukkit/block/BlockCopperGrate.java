package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;


public class BlockCopperGrate extends BlockCopperBase {

    public BlockCopperGrate() {
        // Does nothing
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public String getName() {
        return "Copper Grate";
    }

    @Override
    public int getId() {
        return COPPER_GRATE;
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
                return waxed ? WAXED_COPPER_GRATE : COPPER_GRATE;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_COPPER_GRATE : EXPOSED_COPPER_GRATE;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_COPPER_GRATE : WEATHERED_COPPER_GRATE;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_COPPER_GRATE : OXIDIZED_COPPER_GRATE;
            default:
                return this.getId();
        }
    }
}
