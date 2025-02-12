package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;

import java.util.Locale;

public class BlockDoubleSlabCopperCut extends BlockDoubleSlabCopperBase {
    
    public BlockDoubleSlabCopperCut() {
        this(0);
    }
    
    public BlockDoubleSlabCopperCut(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DOUBLE_CUT_COPPER_SLAB;
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
    public int getSingleSlabId() {
        return CUT_COPPER_SLAB;
    }

    @Override
    public int getItemDamage() {
        return 0;
    }

    @Override
    protected int getCopperId(boolean waxed, OxidizationLevel oxidizationLevel) {
        if (oxidizationLevel == null) {
            return getId();
        }
        switch (oxidizationLevel) {
            case UNAFFECTED:
                return waxed? WAXED_DOUBLE_CUT_COPPER_SLAB : DOUBLE_CUT_COPPER_SLAB;
            case EXPOSED:
                return waxed? WAXED_EXPOSED_DOUBLE_CUT_COPPER_SLAB : EXPOSED_DOUBLE_CUT_COPPER_SLAB;
            case WEATHERED:
                return waxed? WAXED_WEATHERED_DOUBLE_CUT_COPPER_SLAB : WEATHERED_DOUBLE_CUT_COPPER_SLAB;
            case OXIDIZED:
                return waxed? WAXED_OXIDIZED_DOUBLE_CUT_COPPER_SLAB : OXIDIZED_DOUBLE_CUT_COPPER_SLAB;
            default:
                return getId();
        }
    }

    @Override
    public OxidizationLevel getOxidizationLevel() {
        return OxidizationLevel.UNAFFECTED;
    }
}
