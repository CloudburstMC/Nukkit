package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
public class BlockPotato extends BlockCrops {

    public BlockPotato(int meta) {
        super(meta);
    }

    public BlockPotato() {
        this(0);
    }

    @Override
    public String getName() {
        return "Potato Block";
    }

    @Override
    public int getId() {
        return POTATO_BLOCK;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.POTATO);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (!isFullyGrown()) {
            return new Item[]{
                    Item.get(ItemID.POTATO)
            };
        }
        
        int drops = 2;
        int attempts = 3 + Math.min(0, item.getEnchantmentLevel(Enchantment.ID_FORTUNE_DIGGING));
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < attempts; i++) {
            if (random.nextInt(7) < 4) { // 4/7, 0.57142857142857142857142857142857
                drops++;
            }
        }
        
        if (random.nextInt(5) < 1) { // 1/5, 0.2
            return new Item[]{
                    Item.get(ItemID.POTATO,0, drops),
                    Item.get(ItemID.POISONOUS_POTATO)
            };
        } else {
            return new Item[]{
                    Item.get(ItemID.POTATO, 0, drops)
            };
        }
    }
}
