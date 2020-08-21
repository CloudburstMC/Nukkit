package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockTrapdoorAcacia extends BlockTrapdoor {
    public BlockTrapdoorAcacia() {
        this(0);
    }
    
    public BlockTrapdoorAcacia(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return ACACIA_TRAPDOOR;
    }
    
    @Override
    public String getName() {
        return "Acacia Trapdoor";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
