package cn.nukkit.item.enchantment.bow;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public abstract class EnchantmentBow extends Enchantment {
    @PowerNukkitOnly("Re-added for backward compatibility")
    @Deprecated @DeprecationDetails(since = "1.4.0.0-PN", by = "Cloudburst Nukkit",
            reason = "The signature was changed and it doesn't exists anymore in Cloudburst Nukkit",
            replaceWith = "EnchantmentBow(int id, String name, Rarity rarity)")
    protected EnchantmentBow(int id, String name, int weight) {
        this(id, name, Rarity.fromWeight(weight));
    }

    @Since("1.4.0.0-PN")
    protected EnchantmentBow(int id, String name, Rarity rarity) {
        super(id, name, rarity, EnchantmentType.BOW);
    }
}
