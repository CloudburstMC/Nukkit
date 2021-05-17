package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.utils.BlockColor;

/**
 * @author good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockOreGoldNether extends BlockSolid {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockOreGoldNether() {
        // Does nothing
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
        return 3;
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
        if (!item.isPickaxe() || item.getTier() < ItemTool.TIER_WOODEN) {
            return Item.EMPTY_ARRAY;
        }

        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortune = 0;
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        NukkitRandom nukkitRandom = new NukkitRandom();
        int count = nukkitRandom.nextRange(2, 6);
        switch (fortune) {
            case 0:
                // Does nothing
                break;
            case 1:
                if (nukkitRandom.nextRange(0, 2) == 0) {
                    count *= 2;
                }
                break;
            case 2:
                if (nukkitRandom.nextRange(0, 1) == 0) {
                    count *= nukkitRandom.nextRange(2, 3);
                }
                break;
            default:
            case 3:
                if (nukkitRandom.nextRange(0, 4) < 3) {
                    count *= nukkitRandom.nextRange(2, 4);
                }
                break;
        }

        return new Item[]{ Item.get(ItemID.GOLD_NUGGET, 0, count) };
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
