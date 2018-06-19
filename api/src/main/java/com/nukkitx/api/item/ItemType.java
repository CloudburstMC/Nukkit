package com.nukkitx.api.item;

import com.nukkitx.api.metadata.Metadata;

import java.util.Optional;

public interface ItemType {
    int getId();

    String getName();

    boolean isBlock();

    Class<? extends Metadata> getMetadataClass();

    int getMaximumStackSize();

    default boolean isStackable() {
        return getMaximumStackSize() > 1;
    }

    Optional<ToolType> getToolType();

    Optional<TierType> getTierType();
}
