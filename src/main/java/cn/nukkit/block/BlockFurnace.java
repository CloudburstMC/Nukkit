package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning {

    public BlockFurnace(int id, int meta) {
        super(id, meta);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getLightLevel() {
        return 0;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}