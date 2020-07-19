package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;

public class BlockWoodStrippedAcacia extends BlockWoodStripped {
    public BlockWoodStrippedAcacia() {
        this(0);
    }
    
    public BlockWoodStrippedAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.ACACIA;
    }
}
