package cn.nukkit.item.enchantment.loot;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentLoot extends Enchantment {

    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit",
            replaceWith = "EnchantmentLoot(int id, String name, Rarity rarity, EnchantmentType type)")
    protected EnchantmentLoot(int id, String name, int weight, EnchantmentType type) {
        this(id, name, Rarity.fromWeight(weight), type);
    }

    @Since("1.4.0.0-PN")
    protected EnchantmentLoot(int id, String name, Rarity rarity, EnchantmentType type) {
        super(id, name, rarity, type);
    }

    @Override
    public int getMinEnchantAbility(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return getMinEnchantAbility(level) + 45 + level;
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return super.checkCompatibility(enchantment) && enchantment.id != Enchantment.ID_SILK_TOUCH;
    }
}
