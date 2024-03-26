package cn.nukkit.block;

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
}
