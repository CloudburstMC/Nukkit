package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Furnace extends BurningFurnace {

    public Furnace() {
        this(0);
    }

    public Furnace(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Furnace";
    }

    @Override
    public int getId() {
        return Block.FURNACE;
    }
}
