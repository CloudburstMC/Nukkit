package cn.nukkit.block;

public class BlockBambooMosaic extends BlockBambooPlanks {

    public BlockBambooMosaic() {
        this(0);
    }

    public BlockBambooMosaic(int meta) {
         super(0);
    }

    @Override
    public int getId() {
        return BAMBOO_MOSAIC;
    }

    @Override
    public String getName() {
        return "Bamboo Mosaic";
    }
}