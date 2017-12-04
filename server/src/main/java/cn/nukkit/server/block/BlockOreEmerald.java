package cn.nukkit.server.block;

import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemEmerald;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.math.NukkitRandom;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.server.block in project Nukkit .
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
            int count = 1;
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                int i = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = i + 1;
            }

            return new Item[]{
                    new ItemEmerald(0, count)
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
