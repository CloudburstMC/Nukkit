package cn.nukkit.block;

public class BlockStairsRedNetherBrick extends BlockStairsNetherBrick {

    public BlockStairsRedNetherBrick() {
        this(0);
    }

    public BlockStairsRedNetherBrick(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Red Nether Brick Stairs";
    }

    @Override
    public int getId() {
        return RED_NETHER_BRICK_STAIRS;
    }
}
