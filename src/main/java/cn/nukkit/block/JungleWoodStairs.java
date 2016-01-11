package cn.nukkit.block;

/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class JungleWoodStairs extends WoodStairs {

    public JungleWoodStairs() {
        this(0);
    }

    public JungleWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return JUNGLE_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Jungle Wood Stairs";
    }

}
