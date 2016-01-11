package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Color;

/**
 * Created on 2015/12/2 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class Obsidian extends Solid {

    public Obsidian() {
        this(0);
    }

    public Obsidian(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Obsidian";
    }

    @Override
    public int getId() {
        return OBSIDIAN;
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 50;
    }

    @Override
    public double getResistance() {
        return 2000;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_DIAMOND) {
            return new int[][]{
                    {Item.OBSIDIAN, 0, 1}
            };
        } else {
            return new int[][]{};
        }
    }

    @Override
    public boolean onBreak(Item item) {
        //destroy the nether portal
        Block[] nearby = new Block[]{
                this.getSide(Vector3.SIDE_UP),this.getSide(Vector3.SIDE_DOWN),
                this.getSide(Vector3.SIDE_NORTH),this.getSide(Vector3.SIDE_SOUTH),
                this.getSide(Vector3.SIDE_WEST),this.getSide(Vector3.SIDE_EAST),
        };
        for (Block aNearby : nearby) {
            if (aNearby != null) if (aNearby.getId() == NETHER_PORTAL) {
                aNearby.onBreak(item);
            }
        }
        return super.onBreak(item);
    }

    @Override
    public Color getMapColor() {
        return Color.obsidianColor;
    }
}
