package cn.nukkit.api.item;

import cn.nukkit.api.metadata.Metadata;

public interface ItemType {
    int getId();

    String getName();

    boolean isBlock();

    Class<? extends Metadata> getMetadataClass();

    int getMaximumStackSize();

    default boolean isStackable() {
        return getMaximumStackSize() > 1;
    }
}
