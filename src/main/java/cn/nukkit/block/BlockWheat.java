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
        return new ItemSeedsWheat();
    }

    @Override
    public Item[] getDrops(Item item) {
        // https://minecraft.gamepedia.com/Fortune#Seeds
        if (this.getDamage() < 0x07) {
            return new Item[]{ new ItemSeedsWheat() };
        }
        
        Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
        int fortuneLevel = 0;
        if (fortune != null && fortune.getLevel() >= 1) {
            fortuneLevel = fortune.getLevel();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        int count = 0;
        // Fortune increases the number of tests for the distribution, and thus the maximum number of drops, by 1 per level
        for (int i = 0; i < (3 + fortuneLevel); i++) {
            // The binomial distribution in the default case is created by rolling three times (n=3) with a drop probability of 57%
            if (random.nextInt(100) < 57) {
                count++;
            }
        }

        return new Item[]{ Item.get(ItemID.WHEAT), Item.get(ItemID.WHEAT_SEEDS, 0, count) };
    }
}
