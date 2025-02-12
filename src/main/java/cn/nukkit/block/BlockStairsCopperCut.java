package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;

import java.util.Locale;

public class BlockStairsCopperCut extends BlockStairsCopperBase {
    
    public BlockStairsCopperCut() {
        this(0);
    }
    
    public BlockStairsCopperCut(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String name = "";
        if (this.isWaxed()) {
            name += "Waxed ";
        }

        OxidizationLevel oxidizationLevel = this.getOxidizationLevel();
        if (oxidizationLevel != OxidizationLevel.UNAFFECTED) {
            String oxidationName = oxidizationLevel.name();
            name += oxidationName.charAt(0) + oxidationName.substring(1).toLowerCase(Locale.ROOT);
        }
        return name + " Cut Copper Stairs";
    }

    @Override
    public int getId() {
        return CUT_COPPER_STAIRS;
    }


    @Override
    protected int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return this.getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed ? WAXED_CUT_COPPER_STAIRS : CUT_COPPER_STAIRS;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_CUT_COPPER_STAIRS : EXPOSED_CUT_COPPER_STAIRS;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_CUT_COPPER_STAIRS : WEATHERED_CUT_COPPER_STAIRS;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_CUT_COPPER_STAIRS : OXIDIZED_CUT_COPPER_STAIRS;
            default:
                return this.getId();
        }
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
