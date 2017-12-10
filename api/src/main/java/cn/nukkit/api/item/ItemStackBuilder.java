package cn.nukkit.api.item;

import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;

public interface ItemStackBuilder {

    ItemStackBuilder itemType(@Nonnull ItemType itemType);

    ItemStackBuilder amount(int amount);

    ItemStackBuilder name(@Nonnull String name);

    ItemStackBuilder clearName();

    ItemStackBuilder itemData(Metadata data);

    ItemStack build();
}
