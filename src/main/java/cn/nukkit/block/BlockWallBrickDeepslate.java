package cn.nukkit.block;

public class BlockWallBrickDeepslate extends BlockWall {

    public BlockWallBrickDeepslate() {
        this(0);
    }

    public BlockWallBrickDeepslate(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Deepslate Brick Wall";
    }

    @Override
    public int getId() {
        return DEEPSLATE_BRICK_WALL;
    }
}
