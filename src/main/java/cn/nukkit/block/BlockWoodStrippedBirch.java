package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;

public class BlockWoodStrippedBirch extends BlockWoodStripped {
    public BlockWoodStrippedBirch() {
        this(0);
    }
    
    public BlockWoodStrippedBirch(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_BIRCH_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.BIRCH;
    }
}
