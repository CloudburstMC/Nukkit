package cn.nukkit.block;

public class BlockPolishedBlackstoneBrickWall extends BlockWall {

    public BlockPolishedBlackstoneBrickWall() {
        this(0);
    }

    public BlockPolishedBlackstoneBrickWall(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Wall";
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_WALL;
    }
}
