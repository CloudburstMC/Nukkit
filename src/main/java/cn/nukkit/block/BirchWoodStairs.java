package cn.nukkit.block;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BirchWoodStairs extends WoodStairs {

    public BirchWoodStairs() {
        this(0);
    }

    public BirchWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BIRCH_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Birch Wood Stairs";
    }

}
