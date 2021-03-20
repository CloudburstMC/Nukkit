package cn.nukkit.item.enchantment.damage;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentDamage extends Enchantment {

    public enum TYPE {
        ALL,
        SMITE,
        ARTHROPODS
    }

    protected final TYPE damageType;

    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit", 
            reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit",
            replaceWith = "EnchantmentDamage(int id, String name, Rarity rarity, TYPE type)")
    protected EnchantmentDamage(int id, String name, int weight, TYPE type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }
    
    @Since("1.4.0.0-PN")
    protected EnchantmentDamage(int id, String name, Rarity rarity, TYPE type) {
        super(id, name, rarity, EnchantmentType.SWORD);
        this.damageType = type;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
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

    @Override
    public boolean isItemAcceptable(Item item) {
        if (item.isAxe()) {
            return true;
        }
        return super.isItemAcceptable(item);
    }
}
