package cn.nukkit.item.enchantment;

public class EnchantmentLunge extends Enchantment {

    protected EnchantmentLunge() {
        super(ID_LUNGE, "lunge", Rarity.UNCOMMON, EnchantmentType.SPEAR);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
