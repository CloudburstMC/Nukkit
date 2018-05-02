package com.nukkitx.api.block;

import com.nukkitx.api.item.ItemType;

public interface BlockType extends ItemType {

    @Override
    default boolean isBlock() {
        return true;
    }

    boolean isDiggable();

    boolean isTransparent();

    boolean isFlammable();

    int emitsLight();

    int filtersLight();

    float hardness();

    boolean isFloodable();

    boolean isSolid();

    int burnChance();

    int burnability();

    float resistance();
}
