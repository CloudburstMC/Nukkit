package cn.nukkit.block;

public class BlockStairsSmoothQuartz extends BlockStairsQuartz {

    public BlockStairsSmoothQuartz() {
        this(0);
    }

    public BlockStairsSmoothQuartz(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Smooth Quartz Stairs";
    }

    @Override
    public int getId() {
        return SMOOTH_QUARTZ_STAIRS;
    }
}
