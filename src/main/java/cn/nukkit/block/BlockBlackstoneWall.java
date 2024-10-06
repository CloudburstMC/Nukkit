package cn.nukkit.block;

public class BlockBlackstoneWall extends BlockWall {

    public BlockBlackstoneWall() {
        this(0);
    }

    public BlockBlackstoneWall(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Blackstone Wall";
    }

    @Override
    public int getId() {
        return BLACKSTONE_WALL;
    }
}
