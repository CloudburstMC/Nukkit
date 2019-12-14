package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockWaterStill extends BlockWater {

    public BlockWaterStill(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Still Water";
    }

    @Override
    public Block getBlock(int meta) {
        return Block.get(STILL_WATER, meta);
    }

}
