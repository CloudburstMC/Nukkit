package cn.nukkit.block;

public class BlockBambooMosaicStairs extends BlockBambooStairs {

    public BlockBambooMosaicStairs() {
        this(0);
    }

    public BlockBambooMosaicStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC_STAIRS;
    }

    @Override
    public String getName() {
        return "Bamboo Mosaic Stairs";
    }
}
