package cn.nukkit.block;

public class BlockStairsAndesitePolished extends BlockStairsAndesite {

    public BlockStairsAndesitePolished() {
        this(0);
    }

    public BlockStairsAndesitePolished(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Polished Andesite Stairs";
    }

    @Override
    public int getId() {
        return POLISHED_ANDESITE_STAIRS;
    }
}
