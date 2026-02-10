package cn.nukkit.block;

public class BlockWallTuff extends BlockWall {

    public BlockWallTuff() {
        this(0);
    }

    public BlockWallTuff(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Tuff Wall";
    }

    @Override
    public int getId() {
        return TUFF_WALL;
    }
}
