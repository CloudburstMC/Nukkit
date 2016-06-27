package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockFurnace extends BlockFurnaceBurning {

    public BlockFurnace() {
        this(0);
    }

    public BlockFurnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getId() {
        return FURNACE;
    }
}
