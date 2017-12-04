package cn.nukkit.server.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockWaterStill extends BlockWater {

    public BlockWaterStill() {
        super(0);
    }

    public BlockWaterStill(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STILL_WATER;
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public BlockLiquid getBlock(int meta) {
        return new BlockWaterStill(meta);
    }

}
