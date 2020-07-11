package cn.nukkit.blockproperty.values;

import cn.nukkit.blockproperty.ArrayBlockProperty;

public enum StoneSlab4Type {
    MOSSY_STONE_BRICK,
    SMOOTH_QUARTZ,
    STONE,
    CUT_SANDSTONE,
    CUT_RED_SANDSTONE;
    public static final ArrayBlockProperty<StoneSlab4Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_4", values());
}
