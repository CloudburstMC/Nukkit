package com.nukkitx.server.item;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemInstanceBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.metadata.Metadata;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Nonnull
@ToString
public class NukkitItemInstanceBuilder implements ItemInstanceBuilder {
    private ItemType itemType;
    private int amount = 1;
    private Metadata data;
    private String itemName;
    private List<String> itemLore;
    private final Set<EnchantmentInstance> enchantments = new HashSet<>();

    public NukkitItemInstanceBuilder() {
    }

    public NukkitItemInstanceBuilder(@Nonnull ItemInstance item) {
        itemType = item.getItemType();
        amount = item.getAmount();
        data = item.getMetadata().orElse(null);
        itemLore = item.getLore();
        enchantments.addAll(item.getEnchantments());
    }

    @Override
    public ItemInstanceBuilder itemType(@Nonnull ItemType itemType) {
        Preconditions.checkNotNull(itemType, "itemType");
        this.itemType = itemType;
        this.data = null; // If ItemType changed, we can't use the same data.
        return this;
    }

    @Override
    public ItemInstanceBuilder amount(int amount) {
        Preconditions.checkState(itemType != null, "ItemType has not been set");
        Preconditions.checkArgument(amount >= 0 && amount <= itemType.getMaximumStackSize(), "Amount %s is not between 0 and %s", amount, itemType.getMaximumStackSize());
        this.amount = amount;
        return this;
    }

    @Override
    public ItemInstanceBuilder name(@Nonnull String itemName) {
        Preconditions.checkNotNull(itemName, "name");
        this.itemName = itemName;
        return this;
    }

    @Override
    public ItemInstanceBuilder clearName() {
        this.itemName = null;
        return this;
    }

    @Override
    public ItemInstanceBuilder lore(List<String> lines) {
        Preconditions.checkNotNull(lines, "lines");
        this.itemLore = ImmutableList.copyOf(lines);
        return this;
    }

    @Override
    public ItemInstanceBuilder clearLore() {
        this.itemLore = null;
        return this;
    }

    @Override
    public ItemInstanceBuilder itemData(Metadata data) {
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
    public ItemInstanceBuilder addEnchantment(EnchantmentInstance enchantment) {
        addEnchantment0(enchantment);
        return this;
    }

    @Override
    public ItemInstanceBuilder addEnchantments(Collection<EnchantmentInstance> enchantments) {
        Preconditions.checkNotNull(enchantments, "enchantments");
        enchantments.forEach(this::addEnchantment0);
        return this;
    }

    @Override
    public ItemInstanceBuilder clearEnchantments() {
        enchantments.clear();
        return this;
    }

    @Override
    public ItemInstanceBuilder removeEnchantment(EnchantmentInstance enchantment) {
        enchantments.remove(enchantment);
        return this;
    }

    @Override
    public ItemInstanceBuilder removeEnchantments(Collection<EnchantmentInstance> enchantments) {
        Preconditions.checkNotNull(enchantments, "enchantments");
        this.enchantments.removeAll(enchantments);
        return this;
    }

    @Override
    public ItemInstance build() {
        Preconditions.checkArgument(itemType != null, "ItemType has not been set");
        return new NukkitItemInstance(itemType, amount, data, itemName, itemLore, enchantments);
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
