package com.nukkitx.api.item;

import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface RecipeItemInstanceBuilder extends ItemInstanceBuilder {

    @Override
    RecipeItemInstanceBuilder itemType(@Nonnull ItemType itemType);

    @Override
    RecipeItemInstanceBuilder amount(int amount);

    @Override
    RecipeItemInstanceBuilder name(@Nonnull String name);

    @Override
    RecipeItemInstanceBuilder clearName();

    @Override
    RecipeItemInstanceBuilder lore(List<String> lines);

    @Override
    RecipeItemInstanceBuilder clearLore();

    @Override
    RecipeItemInstanceBuilder itemData(Metadata data);

    @Override
    RecipeItemInstanceBuilder addEnchantment(EnchantmentInstance enchantment);

    @Override
    RecipeItemInstanceBuilder addEnchantments(Collection<EnchantmentInstance> enchantmentInstanceCollection);

    @Override
    RecipeItemInstanceBuilder clearEnchantments();

    @Override
    RecipeItemInstanceBuilder removeEnchantment(EnchantmentInstance enchantment);

    @Override
    RecipeItemInstanceBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments);

    RecipeItemInstanceBuilder ignoreMetadata(boolean ignoreMetadata);

    @Override
    RecipeItemInstance build();
}
