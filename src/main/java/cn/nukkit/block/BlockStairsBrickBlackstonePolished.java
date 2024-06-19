package cn.nukkit.block;

public class BlockStairsBrickBlackstonePolished extends BlockStairsBlackstonePolished {

    public BlockStairsBrickBlackstonePolished() {
        this(0);
    }

    public BlockStairsBrickBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_BRICK_STAIRS;
    }

    @Override
    public String getName() {
        return "Polished Blackstone Brick Stairs";
    }
}
