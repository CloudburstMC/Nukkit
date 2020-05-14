package cn.nukkit.item.enchantment.damage;

import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public abstract class EnchantmentDamage extends Enchantment {

    public enum TYPE {
        ALL,
        SMITE,
        ARTHROPODS
    }

    protected final TYPE damageType;
    
    private PowerNukkit powerNukkit;

    protected EnchantmentDamage(int id, String name, int weight, TYPE type) {
        super(id, name, weight, EnchantmentType.SWORD);
        this.damageType = type;
    }

    @Override
    public boolean isCompatibleWith(Enchantment enchantment) {
        return !(enchantment instanceof EnchantmentDamage);
    }

    @Override
    public boolean canEnchant(Item item) {
        return item.isAxe() || super.canEnchant(item);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public String getName() {
        return "%enchantment.damage." + this.name;
    }

    @Override
    public boolean isMajor() {
        return true;
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
        @Override
        public boolean isItemAcceptable(Item item) {
            if (item.isAxe()) {
                return true;
            }
            return super.isItemAcceptable(item);
        }
    }
}
