package cn.nukkit.block;

public class BlockWallTuffPolished extends BlockWall {

    public BlockWallTuffPolished() {
        this(0);
    }

    public BlockWallTuffPolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Tuff Wall";
    }

    @Override
    public int getId() {
        return POLISHED_TUFF_WALL;
    }
}
