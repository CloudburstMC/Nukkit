package cn.nukkit.item.enchantment;

import cn.nukkit.item.Item;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentSilkTouch extends Enchantment {
    private PowerNukkit powerNukkit;
    
    protected EnchantmentSilkTouch() {
        super(ID_SILK_TOUCH, "untouching", 1, EnchantmentType.DIGGER);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 15;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return this.getMinEnchantAbility(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return super.isCompatibleWith(enchantment) && enchantment.id != ID_FORTUNE_DIGGING;
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isShears() || super.canEnchant(item);
    }
    
    
    /**
     * @since 1.2.1.0-PN
     */
    @Override
    public PowerNukkit getPowerNukkit() {
        PowerNukkit powerNukkit = this.powerNukkit;
        if (powerNukkit == null) this.powerNukkit = powerNukkit = new PowerNukkit();
        return powerNukkit;
    }

    /**
     * @since 1.2.1.0-PN
     */
    public class PowerNukkit extends Enchantment.PowerNukkit {
        /**
         * @since 1.2.1.0-PN
         */
        @Override
        public boolean isItemAcceptable(Item item) {
            if (item.isShears()) {
                return true;
            }
            return super.isItemAcceptable(item);
        }
    }
}
