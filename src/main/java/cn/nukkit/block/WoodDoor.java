package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class WoodDoor extends Door {

    public WoodDoor() {
        this(0);
    }

    public WoodDoor(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Wood Door Block";
    }

    @Override
    public int getId() {
        return WOOD_DOOR_BLOCK;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public int[][] getDrops(Item item) {
        return new int[][]{
                {Item.WOODEN_DOOR, 0, 1}
        };
    }

    @Override
    public Color getColor() {
        return Color.woodColor;
    }
}
