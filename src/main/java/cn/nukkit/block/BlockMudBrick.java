package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMudBrick extends BlockSolid {

    public BlockMudBrick() {
    }

    @Override
    public String getName() {
        return "Mud Brick";
    }

    @Override
    public int getId() {
        return MUD_BRICKS;
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
