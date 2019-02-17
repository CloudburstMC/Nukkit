package com.nukkitx.server.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemStackBuilder;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.RecipeItemStackBuilder;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.StringTag;
import com.nukkitx.server.metadata.MetadataSerializers;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.*;

@ToString
@Immutable
public class NukkitItemStack implements ItemStack {
    private final ItemType type;
    private final int amount;
    private final Metadata data;
    private final String itemName;
    private final List<String> itemLore;
    private final Set<EnchantmentInstance> enchantments;

    public NukkitItemStack(ItemType type) {
        this(type, 1, null, null, null, null);
    }

    public NukkitItemStack(ItemType type, int amount, Metadata data) {
        this(type, amount, data, null, null, null);
    }

    public NukkitItemStack(ItemType type, int amount, Metadata data, String itemName, List<String> itemLore, Collection<EnchantmentInstance> enchantments) {
        this.type = type;
        this.amount = amount;
        this.data = data;
        this.itemName = itemName;
        this.itemLore = itemLore == null ? new ArrayList<>() : itemLore;
        this.enchantments = enchantments == null ? ImmutableSet.of() : ImmutableSet.copyOf(enchantments);
    }

    @Override
    public ItemType getItemType() {
        return type;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<Metadata> getMetadata() {
        return Optional.ofNullable(data);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(itemName);
    }

    @Override
    public List<String> getLore() {
        return ImmutableList.copyOf(itemLore);
    }

    @Override
    public Collection<EnchantmentInstance> getEnchantments() {
        return ImmutableList.copyOf(enchantments);
    }

    @Override
    public ItemStackBuilder toBuilder() {
        return new NukkitItemStackBuilder(this);
    }

    @Override
    public RecipeItemStackBuilder toRecipeBuilder() {
        return new NukkitRecipeItemStackBuilder(this);
    }

    @Override
    public boolean isMergeable(@Nonnull ItemStack other) {
        return equals(other, false, true, true);
    }

    @Override
    public boolean equals(@Nullable ItemStack item) {
        return equals(item, true, true, true);
    }

    @Override
    public boolean isSimilar(@Nonnull ItemStack other) {
        return equals(other, false, true, false);
    }

    @Override
    public boolean equals(@Nullable ItemStack other, boolean checkAmount, boolean checkMeta, boolean checkUserData) {
        if (this == other) return true;
        if (other == null) return false;
        NukkitItemStack that = (NukkitItemStack) other;
        return this.type == that.type && (!checkAmount || this.amount == that.amount) &&
                (!checkUserData || Objects.equals(this.itemName, that.itemName) && Objects.equals(this.enchantments, that.enchantments)) &&
                (!checkMeta || Objects.equals(this.data, that.data)); // TODO: Custom data.
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemStack)) return false;
        return equals((ItemStack) o, true, true, true);
    }

    public int hashCode() {
        return Objects.hash(type, amount, data, itemName, itemLore, enchantments);
    }

    public CompoundTag toFullNBT() {
        return CompoundTagBuilder.builder()
                .byteTag("Count", (byte) amount)
                .shortTag("Damage", MetadataSerializers.serializeMetadata(this))
                .shortTag("id", (short) type.getId())
                .tag(toSpecificNBT())
                .buildRootTag();
    }

    public CompoundTag toSpecificNBT() {
        CompoundTagBuilder display = CompoundTagBuilder.builder();
        // Custom Name
        if (itemName != null) {
            display.stringTag("Name", itemName);
        }

        // Lore
        if (itemLore != null) {
            List<StringTag> loreLines = new ArrayList<>();
            itemLore.forEach(s -> loreLines.add(new StringTag("", s)));
            display.listTag("Lore", StringTag.class, loreLines);
        }
        return display.build("display");
    }
}
