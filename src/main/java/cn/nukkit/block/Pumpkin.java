package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Pumpkin extends Solid {
    public Pumpkin() {
        this(0);
    }

    public Pumpkin(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Pumpkin";
    }

    @Override
    public int getId() {
        return PUMPKIN;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_AXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, int face, double fx, double fy, double fz, Player player) {
        if (player != null) {
            if (player.getDirection() != null) {
                this.meta = (player.getDirection() + 5) % 4;
            }
        }
        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public Color getColor() {
        return Color.foliageColor;
    }
}
