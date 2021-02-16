package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

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
        return item.getId() == Item.SKULL || item.getId() == Item.COMPASS || super.canEnchant(item);
    }
}
