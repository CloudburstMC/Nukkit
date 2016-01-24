package cn.nukkit.block;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class DarkOakWoodStairs extends WoodStairs {

    public DarkOakWoodStairs() {
        this(0);
    }

    public DarkOakWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DARK_OAK_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Dark Oak Wood Stairs";
    }

}
