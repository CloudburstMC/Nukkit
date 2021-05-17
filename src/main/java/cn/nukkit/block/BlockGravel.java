package cn.nukkit.block;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemFlint;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.NukkitRandom;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class BlockGravel extends BlockFallable {

    public BlockGravel() {
        // Does Nothing
    }

    @Override
    public int getId() {
        return GRAVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    public Item[] getDrops(Item item) {
        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortune = 0;
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }
        
        NukkitRandom nukkitRandom = new NukkitRandom();
        switch (fortune) {
            case 0:
                if (nukkitRandom.nextRange(0, 9) == 0) {
                    return new Item[]{ Item.get(ItemID.FLINT, 0, 1) };
                    
                }
                break;
            case 1:
                if (nukkitRandom.nextRange(0, 6) == 0) {
                    return new Item[]{ Item.get(ItemID.FLINT, 0, 1) };
                }
                break;
            case 2:
                if (nukkitRandom.nextRange(0, 3) == 0) {
                    return new Item[]{ Item.get(ItemID.FLINT, 0, 1) };
                }
                break;
            default:
            case 3:
                return new Item[]{ Item.get(ItemID.FLINT, 0, 1) };
        }
        return new Item[]{ toItem() };
    }
    
    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
