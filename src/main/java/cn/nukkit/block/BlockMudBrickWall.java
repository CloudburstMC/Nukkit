package cn.nukkit.block;

import cn.nukkit.utils.BlockColor;

public class BlockMudBrickWall extends BlockWall {

    public BlockMudBrickWall() {
        this(0);
    }

    public BlockMudBrickWall(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Mud Brick Wall";
    }

    @Override
    public int getId() {
        return MUD_BRICK_WALL;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_TERRACOTA_BLOCK_COLOR;
    }
}
