package cn.nukkit.block;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemNuggetGold;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
public class BlockNetherOreGold extends BlockSolid {

    public BlockNetherOreGold() {
    }

    @Override
    public int getId() {
        return NETHER_GOLD_ORE;
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
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return "Nether Gold Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            int count = new NukkitRandom().nextRange(2, 6);
            
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null) {
                if (fortune.getLevel() == 1) {
                    if (new NukkitRandom().nextRange(0, 2) == 0) {
                        count = count * 2;
                    }
                } else if (fortune.getLevel() == 2) {
                    if (new NukkitRandom().nextRange(0, 1) == 0) {
                        count = count * new NukkitRandom().nextRange(2, 3);
                    }
                } else if (fortune.getLevel() == 3) {
                    if (new NukkitRandom().nextRange(0, 4) < 3) {
                        count = count * new NukkitRandom().nextRange(2, 4);
                    }
                }
            }
            
            return new Item[]{
                new ItemNuggetGold(0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.NETHERRACK_BLOCK_COLOR;
    }
}
