package com.nukkitx.api.item;

import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface RecipeItemStackBuilder extends ItemStackBuilder {

    @Override
    RecipeItemStackBuilder itemType(@Nonnull ItemType itemType);

    @Override
    RecipeItemStackBuilder amount(int amount);

    @Override
    RecipeItemStackBuilder name(@Nonnull String name);

    @Override
    RecipeItemStackBuilder clearName();

    @Override
    RecipeItemStackBuilder lore(List<String> lines);

    @Override
    RecipeItemStackBuilder clearLore();

    @Override
    RecipeItemStackBuilder itemData(Metadata data);

    @Override
    RecipeItemStackBuilder addEnchantment(EnchantmentInstance enchantment);

    @Override
    RecipeItemStackBuilder addEnchantments(Collection<EnchantmentInstance> enchantmentInstanceCollection);

    @Override
    RecipeItemStackBuilder clearEnchantments();

    @Override
    RecipeItemStackBuilder removeEnchantment(EnchantmentInstance enchantment);

    @Override
    RecipeItemStackBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments);

    RecipeItemStackBuilder ignoreMetadata(boolean ignoreMetadata);

    @Override
    RecipeItemStack build();
}
