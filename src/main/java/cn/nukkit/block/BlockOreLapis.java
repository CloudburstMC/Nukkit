package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockOreLapis extends BlockOre {

    @Override
    public int getId() {
        return LAPIS_ORE;
    }

    @Override
    public String getName() {
        return "Lapis Lazuli Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= this.getToolTier()) {
            if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
                return new Item[]{this.toItem()};
            }

            int count = 4 + ThreadLocalRandom.current().nextInt(6);
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            if (fortune != null && fortune.getLevel() >= 1) {
                int i = ThreadLocalRandom.current().nextInt(fortune.getLevel() + 2) - 1;

                if (i < 0) {
                    i = 0;
                }

                count = count + i;
            }

            return new Item[]{
                    Item.get(Item.DYE, 4, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.DYE;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(2, 5);
    }
}
