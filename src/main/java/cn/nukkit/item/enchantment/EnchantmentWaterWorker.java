package cn.nukkit.item.enchantment;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentWaterWorker extends Enchantment {
    protected EnchantmentWaterWorker() {
        super(ID_WATER_WORKER, "waterWorker", 2, EnchantmentType.ARMOR_HEAD);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 1;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 40;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }
}
