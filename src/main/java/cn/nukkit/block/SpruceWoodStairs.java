package cn.nukkit.block;
/**
 * Created on 2015/11/25 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class SpruceWoodStairs extends WoodStairs {

    public SpruceWoodStairs() {
        this(0);
    }

    public SpruceWoodStairs(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SPRUCE_WOOD_STAIRS;
    }

    @Override
    public String getName() {
        return "Spruce Wood Stairs";
    }

}
