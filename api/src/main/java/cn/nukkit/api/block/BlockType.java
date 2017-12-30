package cn.nukkit.api.block;

import cn.nukkit.api.item.ItemType;

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
}
