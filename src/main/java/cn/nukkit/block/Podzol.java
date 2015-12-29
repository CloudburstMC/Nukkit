package cn.nukkit.block;

import cn.nukkit.item.Tool;

/**
 * Created on 2015/11/22 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Podzol extends Solid {

    public Podzol() {
        this(0);
    }

    public Podzol(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return PODZOL;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Podzol";
    }

    @Override
    public double getHardness() {
        return 2.5;
    }
}
