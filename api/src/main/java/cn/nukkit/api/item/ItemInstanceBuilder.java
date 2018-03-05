package cn.nukkit.api.item;

import cn.nukkit.api.enchantment.EnchantmentInstance;
import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface ItemInstanceBuilder {

    ItemInstanceBuilder itemType(@Nonnull ItemType itemType);

    ItemInstanceBuilder amount(int amount);

    ItemInstanceBuilder name(@Nonnull String name);

    ItemInstanceBuilder clearName();

    ItemInstanceBuilder lore(List<String> lines);

    ItemInstanceBuilder clearLore();

    ItemInstanceBuilder itemData(Metadata data);

    ItemInstanceBuilder addEnchantment(EnchantmentInstance enchantment);

    ItemInstanceBuilder addEnchantments(Collection<EnchantmentInstance> enchantmentInstanceCollection);

    ItemInstanceBuilder clearEnchantments();

    ItemInstanceBuilder removeEnchantment(EnchantmentInstance enchantment);

    ItemInstanceBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments);

    ItemInstance build();
}
