package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockOreGoldNether extends BlockSolid {

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
        if (!item.isPickaxe()) {
            return new Item[0];
        }

        Enchantment enchantment = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortune = 0;
        if (enchantment != null) {
            fortune = enchantment.getLevel();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = random.nextInt(2, 7);
        switch (fortune) {
            case 0:
                // Does nothing
                break;
            case 1:
                if (random.nextInt(0, 2) == 0) {
                    count *= 2;
                }
                break;
            case 2:
                if (random.nextInt(0, 1) == 0) {
                    count *= random.nextInt(2, 3);
                }
                break;
            default:
            case 3:
                if (random.nextInt(0, 4) < 3) {
                    count *= random.nextInt(2, 4);
                }
                break;
        }

        return new Item[]{ Item.get(Item.GOLD_NUGGET, 0, count) };
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
