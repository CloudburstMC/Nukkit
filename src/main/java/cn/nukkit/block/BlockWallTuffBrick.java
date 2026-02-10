package cn.nukkit.block;

public class BlockWallTuffBrick extends BlockWall {

    public BlockWallTuffBrick() {
        this(0);
    }

    public BlockWallTuffBrick(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Tuff Brick Wall";
    }

    @Override
    public int getId() {
        return TUFF_BRICK_WALL;
    }
}
