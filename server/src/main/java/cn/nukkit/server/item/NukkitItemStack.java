package cn.nukkit.server.item;

import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.item.ItemStackBuilder;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Optional;

@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class NukkitItemStack implements ItemStack {
    private final ItemType itemType;
    private final int amount;
    private final Metadata data;
    private final String itemName;

    public NukkitItemStack(ItemType itemType, int amount, Metadata data) {
        this(itemType, amount, data, null);
    }

    public NukkitItemStack(ItemType itemType, int amount, Metadata data, String itemName) {
        this.itemType = itemType;
        this.amount = amount;
        this.data = (data == null ? itemType.defaultMetadata() : data);
        this.itemName = itemName;
    }

    @Override
    public ItemType getItemType() {
        return itemType;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public Optional<Metadata> getItemData() {
        return Optional.ofNullable(data);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(itemName);
    }

    @Override
    public ItemStackBuilder toBuilder() {
        return new NukkitItemStackBuilder().itemType(itemType).amount(amount).itemData(data).name(itemName);
    }
}
