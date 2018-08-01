package com.nukkitx.server.item;

import com.nukkitx.api.enchantment.EnchantmentInstance;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.RecipeItemInstance;
import com.nukkitx.api.metadata.Metadata;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public class NukkitRecipeItemInstance extends NukkitItemInstance implements RecipeItemInstance {
    private final boolean ignoreMetadata;

    public NukkitRecipeItemInstance(ItemType type, int amount, Metadata data, String itemName, List<String> itemLore, Collection<EnchantmentInstance> enchantments, boolean ignoreMetadata) {
        super(type, amount, data, itemName, itemLore, enchantments);
        this.ignoreMetadata = ignoreMetadata;
    }

    @Override
    public boolean equals(@Nullable ItemInstance item) {
        return equals(item, true, !ignoreMetadata, true);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof ItemInstance)) return false;
        return equals((ItemInstance) o, true, !ignoreMetadata, true);
    }

    @Override
    public boolean isIgnoringMetadata() {
        return ignoreMetadata;
    }
}
