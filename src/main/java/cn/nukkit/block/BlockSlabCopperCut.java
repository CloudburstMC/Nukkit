package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;

import java.util.Locale;

public class BlockSlabCopperCut extends BlockSlabCopperBase {
    
    public BlockSlabCopperCut() {
        this(0);
    }

    public BlockSlabCopperCut(int meta) {
        super(meta, DOUBLE_CUT_COPPER_SLAB);
    }

    protected BlockSlabCopperCut(int meta, int doubleSlab) {
        super(meta, doubleSlab);
    }

    @Override
    public int getId() {
        return CUT_COPPER_SLAB;
    }

    @Override
    public String getSlabName() {
        String name = "";
        if (this.isWaxed()) {
            name += "Waxed ";
        }

        OxidizationLevel oxidizationLevel = this.getOxidizationLevel();
        if (oxidizationLevel != OxidizationLevel.UNAFFECTED) {
            String oxidationName = oxidizationLevel.name();
            name += oxidationName.charAt(0) + oxidationName.substring(1).toLowerCase(Locale.ROOT);
        }
        return name + " Cut Copper";
    }

    @Override
    protected int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed ? WAXED_CUT_COPPER_SLAB : CUT_COPPER_SLAB;
            case EXPOSED:
                return waxed ? WAXED_EXPOSED_CUT_COPPER_SLAB : EXPOSED_CUT_COPPER_SLAB;
            case WEATHERED:
                return waxed ? WAXED_WEATHERED_CUT_COPPER_SLAB : WEATHERED_CUT_COPPER_SLAB;
            case OXIDIZED:
                return waxed ? WAXED_OXIDIZED_CUT_COPPER_SLAB : OXIDIZED_CUT_COPPER_SLAB;
            default:
                return getId();
        }
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
