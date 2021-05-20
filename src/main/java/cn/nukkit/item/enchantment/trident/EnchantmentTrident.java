package cn.nukkit.item.enchantment.trident;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

public abstract class EnchantmentTrident extends Enchantment {
    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit",
            replaceWith = "EnchantmentTrident(int id, String name, Rarity rarity)")
    protected EnchantmentTrident(int id, String name, int weight) {
        this(id, name, Rarity.fromWeight(weight));
    }

    @Since("1.4.0.0-PN")
    protected EnchantmentTrident(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.TRIDENT);
    }

    @Override
    public int getMaxEnchantAbility(int level) {
        return 50;
    }
}
