package cn.nukkit.server.item.enchantment.loot;

import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLootDigging extends EnchantmentLoot {
    public EnchantmentLootDigging() {
        super(Enchantment.ID_FORTUNE_DIGGING, "lootBonusDigger", 2, EnchantmentType.DIGGER);
    }
}
