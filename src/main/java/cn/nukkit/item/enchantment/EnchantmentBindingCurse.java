package cn.nukkit.item.enchantment;

public class EnchantmentBindingCurse extends Enchantment {
    protected EnchantmentBindingCurse() {
        super(ID_BINDING_CURSE, "curse.binding", Rarity.VERY_RARE, EnchantmentType.WEARABLE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 25;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 30;
    }
}
