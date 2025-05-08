package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMudBrickStairs extends BlockStairs {

    public BlockMudBrickStairs() {
        this(0);
    }

    public BlockMudBrickStairs(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mud Brick Stair";
    }

    @Override
    public int getId() {
        return MUD_BRICK_STAIRS;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
