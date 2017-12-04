package cn.nukkit.server.item.enchantment.loot;

import cn.nukkit.server.item.enchantment.Enchantment;
import cn.nukkit.server.item.enchantment.EnchantmentType;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class EnchantmentLootWeapon extends EnchantmentLoot {
    public EnchantmentLootWeapon() {
        super(Enchantment.ID_LOOTING, "lootBonus", 2, EnchantmentType.SWORD);
    }
}
