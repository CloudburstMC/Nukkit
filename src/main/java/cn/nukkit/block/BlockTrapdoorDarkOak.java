package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorDarkOak extends BlockTrapdoor {
    public BlockTrapdoorDarkOak() {
        this(0);
    }
    
    public BlockTrapdoorDarkOak(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return DARK_OAK_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Dark Oak Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_BLOCK_COLOR;
    }
}
