package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Wool extends Solid {

    public Wool() {
        this(0);
    }

    public Wool(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "White Wool",
                "Orange Wool",
                "Magenta Wool",
                "Light Blue Wool",
                "Yellow Wool",
                "Lime Wool",
                "Pink Wool",
                "Gray Wool",
                "Light Gray Wool",
                "Cyan Wool",
                "Purple Wool",
                "Blue Wool",
                "Brown Wool",
                "Green Wool",
                "Red Wool",
                "Black Wool"
        };
        return names[this.meta & 0x0f];
    }

    @Override
    public int getId() {
        return WOOL;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHEARS;
    }

    @Override
    public double getHardness() {
        return 0.8;
    }
}
