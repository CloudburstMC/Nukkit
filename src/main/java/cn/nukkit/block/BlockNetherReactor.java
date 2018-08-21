package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.item.ItemTool;

/**
 * Created by Leonidius20 on 18.08.18.
 */
public class BlockNetherReactor extends BlockSolid {

    @Override
    public String getName() {
        int meta = getDamage();
        if (meta == 1) {
            return "Activated Nether Reactor Core";
        } else if (meta == 2) {
            return "Used Nether Reactor Core";
        }
        return "Nether Reactor Core";
    }

    @Override
    public int getId() {
        return NETHER_REACTOR;
    }

    @Override
    public double getResistance() {
        return 30.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            return new Item[]{new ItemDiamond(0, 3), new ItemIngotIron(0, 6)};
        } else return new Item[]{};
    }
}
