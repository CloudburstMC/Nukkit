package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockOreDiamond extends BlockOre {

    @Override
    public int getId() {
        return DIAMOND_ORE;
    }

    @Override
    public String getName() {
        return "Diamond Ore";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_IRON) {
            if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
                return new Item[]{this.toItem()};
            }

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
                    Item.get(Item.DIAMOND, 0, count)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    protected int getRawMaterial() {
        return ItemID.DIAMOND;
    }

    @Override
    public int getDropExp() {
        return Utils.rand(3, 7);
    }

    @Override
    public int getToolTier() {
        return ItemTool.TIER_IRON;
    }
}
