package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created by Snake1999 on 2016/1/17.
 * Package cn.nukkit.block in project nukkit.
 */
public class Noteblock extends Solid {
    public Noteblock() {
        this(0);
    }

    public Noteblock(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Note Block";
    }

    @Override
    public int getId() {
        return NOTEBLOCK;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public double getHardness() {
        return 0.8D;
    }

    @Override
    public double getResistance() {
        return 4D;
    }

    //todo
}
