package cn.nukkit.block;

import cn.nukkit.blockproperty.value.WoodType;

public class BlockWoodStrippedSpruce extends BlockWoodStripped {
    public BlockWoodStrippedSpruce() {
        this(0);
    }
    
    public BlockWoodStrippedSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRIPPED_SPRUCE_LOG;
    }

    @Override
    public WoodType getWoodType() {
        return WoodType.SPRUCE;
    }
}
