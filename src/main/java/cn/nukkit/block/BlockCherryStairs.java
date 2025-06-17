package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockCherryStairs extends BlockStairsWood {

    public BlockCherryStairs() {
        this(0);
    }

    public BlockCherryStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return CHERRY_STAIRS;
    }

    @Override
    public String getName() {
        return "Cherry Stairs";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WHITE_TERRACOTA_BLOCK_COLOR;
    }
}
