package cn.nukkit.server.item;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.ToString;

import javax.annotation.Nonnull;

@Nonnull
@ToString
public class NukkitItemStackBuilder implements cn.nukkit.api.item.ItemStackBuilder {
    private ItemType itemType;
    private int amount = 1;
    private Metadata data;
    private String itemName;

    @Override
    public cn.nukkit.api.item.ItemStackBuilder itemType(@Nonnull ItemType itemType) {
        Preconditions.checkNotNull(itemType, "itemType");
        this.itemType = itemType;
        this.data = null; // If ItemType changed, we can't use the same data.
        return this;
    }

    @Override
    public cn.nukkit.api.item.ItemStackBuilder amount(int amount) {
        Preconditions.checkState(itemType != null, "ItemType has not been set");
        Preconditions.checkArgument(amount >= 0 && amount <= itemType.getMaximumStackSize(), "Amount %s is not between 0 and %s", amount, itemType.getMaximumStackSize());
        this.amount = amount;
        return this;
    }

    @Override
    public cn.nukkit.api.item.ItemStackBuilder name(@Nonnull String itemName) {
        Preconditions.checkNotNull(itemName, "name");
        this.itemName = itemName;
        return this;
    }

    @Override
    public cn.nukkit.api.item.ItemStackBuilder clearName() {
        this.itemName = null;
        return this;
    }

    @Override
    public cn.nukkit.api.item.ItemStackBuilder itemData(Metadata data) {
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
    public ItemStack build() {
        Preconditions.checkArgument(itemType != null, "ItemType has not been set");
        return new NukkitItemStack(itemType, amount, data, itemName);
    }
}
