package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockWoodStrippedAcacia extends BlockWoodStripped {

    public BlockWoodStrippedAcacia() {
        this(0);
    }

    public BlockWoodStrippedAcacia(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Stripped Acacia Log";
    }

    @Override
    public int getId() {
        return STRIPPED_ACACIA_LOG;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }
}
