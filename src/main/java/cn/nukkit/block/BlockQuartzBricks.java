package cn.nukkit.block;

public class BlockQuartzBricks extends BlockQuartz {

    public BlockQuartzBricks() {
        this(0);
    }

    public BlockQuartzBricks(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Quartz Bricks";
    }

    @Override
    public int getId() {
        return QUARTZ_BRICKS;
    }
}
