package cn.nukkit.block;

public class BlockStairsBlackstonePolished extends BlockStairsBlackstone {

    public BlockStairsBlackstonePolished() {
        this(0);
    }

    public BlockStairsBlackstonePolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Blackstone Stairs";
    }

    @Override
    public int getId() {
        return POLISHED_BLACKSTONE_STAIRS;
    }
}
