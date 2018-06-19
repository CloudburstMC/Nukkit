package com.nukkitx.api.block;

import com.nukkitx.api.item.ItemType;
import com.nukkitx.api.item.TierType;
import com.nukkitx.api.item.ToolType;

import java.util.Optional;

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

    @Override
    default Optional<ToolType> getToolType() {
        return Optional.empty();
    }

    @Override
    default Optional<TierType> getTierType() {
        return Optional.empty();
    }
}
