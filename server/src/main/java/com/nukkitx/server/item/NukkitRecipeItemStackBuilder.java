package com.nukkitx.server.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.RecipeItemStack;
import com.nukkitx.api.item.RecipeItemStackBuilder;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NukkitRecipeItemStackBuilder implements RecipeItemStackBuilder {
    private ItemType itemType;
    private int amount = 1;
    private Metadata data;
    private String itemName;
    private List<String> itemLore;
    private final Set<EnchantmentInstance> enchantments = new HashSet<>();
    private boolean ignoreMetadata = true;

    public NukkitRecipeItemStackBuilder() {
    }

    public NukkitRecipeItemStackBuilder(@Nonnull RecipeItemStack item) {
        Preconditions.checkNotNull(item, "item");
        itemType = item.getItemType();
        amount = item.getAmount();
        data = item.getMetadata().orElse(null);
        itemLore = item.getLore();
        enchantments.addAll(item.getEnchantments());
        ignoreMetadata = item.isIgnoringMetadata();
    }

    public NukkitRecipeItemStackBuilder(@Nonnull ItemStack item) {
        Preconditions.checkNotNull(item, "item");
        itemType = item.getItemType();
        amount = item.getAmount();
        data = item.getMetadata().orElse(null);
        itemLore = item.getLore();
        enchantments.addAll(item.getEnchantments());
    }

    @Override
    public RecipeItemStackBuilder itemType(@Nonnull ItemType itemType) {
        Preconditions.checkNotNull(itemType, "itemType");
        this.itemType = itemType;
        this.data = null; // If ItemType changed, we can't use the same data.
        return this;
    }

    @Override
    public RecipeItemStackBuilder amount(int amount) {
        Preconditions.checkState(itemType != null, "ItemType has not been set");
        Preconditions.checkArgument(amount >= 0 && amount <= itemType.getMaximumStackSize(), "Amount %s is not between 0 and %s", amount, itemType.getMaximumStackSize());
        this.amount = amount;
        return this;
    }

    @Override
    public RecipeItemStackBuilder name(@Nonnull String itemName) {
        Preconditions.checkNotNull(itemName, "name");
        this.itemName = itemName;
        return this;
    }

    @Override
    public RecipeItemStackBuilder clearName() {
        this.itemName = null;
        return this;
    }

    @Override
    public RecipeItemStackBuilder lore(List<String> lines) {
        Preconditions.checkNotNull(lines, "lines");
        this.itemLore = ImmutableList.copyOf(lines);
        return this;
    }

    @Override
    public RecipeItemStackBuilder clearLore() {
        this.itemLore = null;
        return this;
    }

    @Override
    public RecipeItemStackBuilder itemData(Metadata data) {
        if (data != null) {
            Preconditions.checkState(itemType != null, "ItemType has not been set");
            Preconditions.checkArgument(itemType.getMetadataClass() != null, "Item does not have any data associated with it.");
            Preconditions.checkArgument(data.getClass().isAssignableFrom(itemType.getMetadataClass()), "ItemType data is not valid (wanted %s)",
                    itemType.getMetadataClass().getName());
        }
        this.data = data;
        return this;
    }

    @Override
    public RecipeItemStackBuilder addEnchantment(EnchantmentInstance enchantment) {
        addEnchantment0(enchantment);
        return this;
    }

    @Override
    public RecipeItemStackBuilder addEnchantments(Collection<EnchantmentInstance> enchantments) {
        Preconditions.checkNotNull(enchantments, "enchantments");
        enchantments.forEach(this::addEnchantment0);
        return this;
    }

    @Override
    public RecipeItemStackBuilder clearEnchantments() {
        enchantments.clear();
        return this;
    }

    @Override
    public RecipeItemStackBuilder removeEnchantment(EnchantmentInstance enchantment) {
        enchantments.remove(enchantment);
        return this;
    }

    @Override
    public RecipeItemStackBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments) {
        Preconditions.checkNotNull(enchantments, "enchantments");
        this.enchantments.removeAll(enchantments);
        return this;
    }

    @Override
    public RecipeItemStackBuilder ignoreMetadata(boolean ignoreMetadata) {
        this.ignoreMetadata = ignoreMetadata;
        return this;
    }

    @Override
    public RecipeItemStack build() {
        Preconditions.checkArgument(itemType != null, "ItemType has not been set");
        return new NukkitRecipeItemStack(itemType, amount, data, itemName, itemLore, enchantments, ignoreMetadata);
    }

    private void addEnchantment0(EnchantmentInstance enchantment) {
        Preconditions.checkNotNull(enchantment, "enchantment");
        for (EnchantmentInstance ench : enchantments) {
            if (ench.getEnchantmentType() == enchantment.getEnchantmentType()) {
                throw new IllegalArgumentException("Enchantment type is already in builder");
            }
        }
        enchantments.add(enchantment);
    }
}
