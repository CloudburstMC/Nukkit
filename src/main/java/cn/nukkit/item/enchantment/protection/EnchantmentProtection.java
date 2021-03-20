package cn.nukkit.item.enchantment.protection;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemElytra;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

import javax.annotation.Nonnull;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentProtection extends Enchantment {

    public enum TYPE {
        ALL,
        FIRE,
        FALL,
        EXPLOSION,
        PROJECTILE
    }

    protected final TYPE protectionType;

    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit",
            replaceWith = "EnchantmentProtection(int id, String name, Rarity rarity, EnchantmentProtection.TYPE type)")
    protected EnchantmentProtection(int id, String name, int weight, EnchantmentProtection.TYPE type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }

    @Since("1.4.0.0-PN")
    protected EnchantmentProtection(int id, String name, Rarity rarity, EnchantmentProtection.TYPE type) {
        super(id, name, rarity, EnchantmentType.ARMOR);
        this.protectionType = type;
        if (protectionType == TYPE.FALL) {
            this.type = EnchantmentType.ARMOR_FEET;
        }
    }

    @Override
    public boolean canEnchant(@Nonnull Item item) {
        return !(item instanceof ItemElytra) && super.canEnchant(item);
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        if (enchantment instanceof EnchantmentProtection) {
            if (((EnchantmentProtection) enchantment).protectionType == this.protectionType) {
                return false;
            }
            return ((EnchantmentProtection) enchantment).protectionType == TYPE.FALL || this.protectionType == TYPE.FALL;
        }
        return super.checkCompatibility(enchantment);
    }

    @Override
    public int getMaxLevel() {
        return 4;
    }

    @Override
    public String getName() {
        return "%enchantment.protect." + this.name;
    }

    public double getTypeModifier() {
        return 0;
    }

    @Override
    public boolean isMajor() {
        return true;
    }
}
