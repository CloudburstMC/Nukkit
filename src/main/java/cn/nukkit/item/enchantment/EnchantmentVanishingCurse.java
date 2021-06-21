package cn.nukkit.item.enchantment;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

public class EnchantmentVanishingCurse extends Enchantment {
    protected EnchantmentVanishingCurse() {
        super(ID_VANISHING_CURSE, "curse.vanishing", Rarity.VERY_RARE, EnchantmentType.BREAKABLE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }

    @Override
    public boolean canEnchant(Item item) {
        switch (item.getId()) {
            case ItemID.SKULL:
            case ItemID.COMPASS:
                return true;
            default:
                if (item.getId() < 255 && item.getBlock() != null && item.getBlock().getId() == BlockID.CARVED_PUMPKIN) {
                    return true;
                }
                return super.canEnchant(item);
        }
    }
}
