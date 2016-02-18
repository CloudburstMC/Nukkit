package cn.nukkit.item.enchantment.loot;

import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLootFishing extends EnchantmentLoot {
    public EnchantmentLootFishing() {
        super(Enchantment.ID_FORTUNE_FISHING, "lootBonusFishing", 2, EnchantmentType.FISHING_ROD);
    }
}
