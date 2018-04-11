/*
 * GNU GENERAL PUBLIC LICENSE
 * Copyright (C) 2018 NukkitX Project
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * Contact info: info@nukkitx.com
 */

package cn.nukkit.server.item;

import cn.nukkit.api.enchantment.EnchantmentInstance;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemInstanceBuilder;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
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
        return null;
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
