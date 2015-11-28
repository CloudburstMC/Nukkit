package cn.nukkit.block;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class StillWater extends Water {

    public StillWater() {
        super(0);
    }

    public StillWater(int meta) {
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
}
