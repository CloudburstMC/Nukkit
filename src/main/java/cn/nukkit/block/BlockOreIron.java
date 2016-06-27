package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockOreIron extends BlockSolid {


    public BlockOreIron() {
        this(0);
    }

    public BlockOreIron(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return IRON_ORE;
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
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Iron Ore";
    }

    @Override
    public int[][] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_STONE) {
            return new int[][]{
                    {Item.IRON_ORE, 0, 1}
            };
        } else {
            return new int[0][0];
        }
    }
}
