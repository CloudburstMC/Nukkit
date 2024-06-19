package cn.nukkit.block;

public class BlockPolishedBlackstoneWall extends BlockWall {

    public BlockPolishedBlackstoneWall() {
        this(0);
    }

    public BlockPolishedBlackstoneWall(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Wall";
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_WALL;
    }
}
