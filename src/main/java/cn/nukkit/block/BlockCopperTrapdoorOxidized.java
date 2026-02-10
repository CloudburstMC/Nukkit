package cn.nukkit.block;

import cn.nukkit.block.properties.OxidizationLevel;
import cn.nukkit.utils.BlockColor;

public class BlockCopperTrapdoorOxidized extends BlockCopperTrapdoor {

    public BlockCopperTrapdoorOxidized() {
        this(0);
    }

    public BlockCopperTrapdoorOxidized(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Oxidized Copper Trapdoor";
    }

    @Override
    public int getId() {
        return OXIDIZED_COPPER_TRAPDOOR;
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
