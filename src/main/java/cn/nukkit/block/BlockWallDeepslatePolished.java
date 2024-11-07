package cn.nukkit.block;

public class BlockWallDeepslatePolished extends BlockWall {

    public BlockWallDeepslatePolished() {
        this(0);
    }

    public BlockWallDeepslatePolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Deepslate Wall";
    }

    @Override
    public int getId() {
        return POLISHED_DEEPSLATE_WALL;
    }
}
