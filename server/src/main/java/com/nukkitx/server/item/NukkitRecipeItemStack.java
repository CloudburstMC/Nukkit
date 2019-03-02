package com.nukkitx.server.item;

import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.RecipeItemStack;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class NukkitRecipeItemStack extends NukkitItemStack implements RecipeItemStack {
    private final boolean ignoreMetadata;

    public NukkitRecipeItemStack(ItemType type, int amount, Metadata data, String itemName, List<String> itemLore, Collection<EnchantmentInstance> enchantments, boolean ignoreMetadata) {
        super(type, amount, data, itemName, itemLore, enchantments);
        this.ignoreMetadata = ignoreMetadata;
    }

    @Override
    public boolean equals(@Nullable ItemStack item) {
        return equals(item, true, !ignoreMetadata, true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemStack)) return false;
        return equals((ItemStack) o, true, !ignoreMetadata, true);
    }

    @Override
    public boolean isIgnoringMetadata() {
        return ignoreMetadata;
    }
}
