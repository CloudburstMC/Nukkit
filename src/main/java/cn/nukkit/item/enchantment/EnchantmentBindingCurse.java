package cn.nukkit.item.enchantment;

public class EnchantmentBindingCurse extends Enchantment {
    protected EnchantmentBindingCurse() {
        super(ID_BINDING_CURSE, "bindingCurse", 1, EnchantmentType.WEARABLE);
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
    public int getMaxLevel() {
        return 1;
    }
}
