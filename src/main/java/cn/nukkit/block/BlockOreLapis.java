package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.Tool;
import cn.nukkit.math.NukkitRandom;

import java.util.Random;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreLapis extends BlockSolid {


    public BlockOreLapis() {
        this(0);
    }

    public BlockOreLapis(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return LAPIS_ORE;
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
    public int getToolType() {
        return Tool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Lapis Lazuli Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= Tool.TIER_STONE) {
            return new int[][]{new int[]{Item.DYE, 4, new Random().nextInt(4) + 4}};
        } else {
            return new int[0][];
        }
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(2, 5);
    }
}
