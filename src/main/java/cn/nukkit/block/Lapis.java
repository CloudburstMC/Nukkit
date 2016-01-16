package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.utils.BlockColor;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class Lapis extends Solid {


    public Lapis() {
        this(0);
    }

    public Lapis(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LAPIS_BLOCK;
    }

    @Override
    public String getName() {
        return "Lapis Lazuli Block";
    }

    @Override
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_STONE) {
            return new int[][]{new int[]{Item.LAPIS_BLOCK, 0, 1}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.LAPIS_BLOCK_COLOR;
    }

}
