package cn.nukkit.item.enchantment.protection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionFire extends EnchantmentProtection {

    public EnchantmentProtectionFire() {
        super(ID_PROTECTION_FIRE, "fire", 5, TYPE.FIRE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 10 + (level - 1) * 8;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 12;
    }

    @Override
    public double getTypeModifier() {
        return 2;
    }
}
