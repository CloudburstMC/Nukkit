package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorSpruce extends BlockTrapdoor {
    public BlockTrapdoorSpruce() {
        this(0);
    }
    
    public BlockTrapdoorSpruce(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SPRUCE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Spruce Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SPRUCE_BLOCK_COLOR;
    }
}
