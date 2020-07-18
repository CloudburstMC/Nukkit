package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorJungle extends BlockTrapdoor {
    public BlockTrapdoorJungle() {
        this(0);
    }
    
    public BlockTrapdoorJungle(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return JUNGLE_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Jungle Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
