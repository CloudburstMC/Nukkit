package cn.nukkit.blockproperty.values;

import cn.nukkit.blockproperty.ArrayBlockProperty;

public enum StoneSlab1Type {
    SMOOTH_STONE,
    SANDSTONE,
    WOOD,
    COBBLESTONE,
    BRICK,
    STONE_BRICK,
    QUARTZ,
    NETHER_BRICK;
    public static final ArrayBlockProperty<StoneSlab1Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type", values());
}
