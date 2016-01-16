package cn.nukkit.block;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class AcaciaWoodStairs extends WoodStairs {

    public AcaciaWoodStairs() {
        this(0);
    }

    public AcaciaWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ACACIA_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Acacia Wood Stairs";
    }

}
