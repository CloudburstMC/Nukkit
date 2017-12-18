package cn.nukkit.api.item.meta;

import cn.nukkit.api.item.enchantment.Enchantment;

import java.util.List;

/**
 * @author CreeperFace
 */
public interface ItemMeta {

    boolean hasCustomName();

    void setCustomName();

    String getCustomName();

    void clearCustomName();

    void setLore(String... lore);

    List<String> getLore();

    boolean hasEnchantments();

    Enchantment getEnchantment(int id);

    void addEnchantment(Enchantment... enchantments);

    List<Enchantment> getEnchantments();
}
