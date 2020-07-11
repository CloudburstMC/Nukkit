package cn.nukkit.blockproperty.values;

import cn.nukkit.blockproperty.ArrayBlockProperty;

public enum StoneSlab3Type {
    END_STONE_BRICK,
    SMOOTH_RED_SANDSTONE,
    POLISHED_ANDESITE,
    ANDESITE,
    DIORITE,
    POLISHED_DIORITE,
    GRANITE,
    POLISHED_GRANITE;
    public static final ArrayBlockProperty<StoneSlab3Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_3", values());
}
