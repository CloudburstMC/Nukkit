package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemSeedsWheat;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xtypr
 * @since 2015/12/2
 */
public class BlockWheat extends BlockCrops {

    public BlockWheat() {
        this(0);
    }

    public BlockWheat(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Wheat Block";
    }

    @Override
    public int getId() {
        return WHEAT_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SEEDS);
    }

    @Override
    public Item[] getDrops(Item item) {
        // https://minecraft.gamepedia.com/Fortune#Seeds
        if (!isFullyGrown()) {
            return new Item[]{ new ItemSeedsWheat() };
        }
        
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = 0;
        int attempts = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING));
        // Fortune increases the number of tests for the distribution, and thus the maximum number of drops, by 1 per level
        for (int i = 0; i < attempts; i++) {
            // The binomial distribution in the default case is created by rolling three times (n=3) with a drop probability of 57%
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                count++;
            }
        }
        
        if (count > 0) {
            return new Item[]{ Item.get(ItemID.WHEAT), Item.get(ItemID.WHEAT_SEEDS, 0, count) };
        } else {
            return new Item[]{ Item.get(ItemID.WHEAT) };
        }
    }
}
