package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEmerald;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitRandom;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockOreEmerald extends BlockSolid {

    public BlockOreEmerald() {
        this(0);
    }

    public BlockOreEmerald(int meta) {
        super(0);
    }

    @Override
    public String getName() {
        return "Emerald Ore";
    }

    @Override
    public int getId() {
        return EMERALD_ORE;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public double getHardness() {
        return 3;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            return new Item[]{
                    new ItemEmerald()
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public int getDropExp() {
        return new NukkitRandom().nextRange(3, 7);
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
