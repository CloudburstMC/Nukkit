package cn.nukkit.item.enchantment.protection;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentProtectionProjectile extends EnchantmentProtection {

    public EnchantmentProtectionProjectile() {
        super(ID_PROTECTION_PROJECTILE, "projectile", 5, TYPE.PROJECTILE);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 3 + (level - 1) * 6;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 15;
    }
}
