package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * @author Nukkit Project Team
 */
public class Bricks extends Solid {

    public Bricks(int meta) {
        super(meta);
    }

    public Bricks() {
        this(0);
    }

    @Override
    public String getName() {
        return "Bricks";
    }

    @Override
    public int getId() {
        return BRICKS_BLOCK;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 30;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new int[][]{{Item.BRICKS_BLOCK, 0, 1}};
        }
        return new int[][]{{}};
    }

    @Override
    public Color getColor() {
        return Color.stoneColor;
    }
}
