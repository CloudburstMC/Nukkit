package cn.nukkit.block;

import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class StainedClay extends Solid {

    public StainedClay() {
        this(0);
    }

    public StainedClay(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "White Stained Clay",
                "Orange Stained Clay",
                "Magenta Stained Clay",
                "Light Blue Stained Clay",
                "Yellow Stained Clay",
                "Lime Stained Clay",
                "Pink Stained Clay",
                "Gray Stained Clay",
                "Light Gray Stained Clay",
                "Cyan Stained Clay",
                "Purple Stained Clay",
                "Blue Stained Clay",
                "Brown Stained Clay",
                "Green Stained Clay",
                "Red Stained Clay",
                "Black Stained Clay"
        };
        return names[this.meta & 0x0f];
    }

    @Override
    public int getId() {
        return STAINED_CLAY;
    }

    @Override
    public double getHardness() {
        return 1.25;
    }

    @Override
    public double getResistance() {
        return 0.75;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public Color getMapColor() {
        return Color.getDyeColor(meta);
    }

}
