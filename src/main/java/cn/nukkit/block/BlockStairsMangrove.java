package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockStairsMangrove extends BlockStairsWood {

    public BlockStairsMangrove() {
        this(0);
    }

    public BlockStairsMangrove(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mangrove Stairs";
    }

    @Override
    public int getId() {
        return MANGROVE_STAIRS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.RED_BLOCK_COLOR;
    }
}
