package cn.nukkit.blockproperty.values;

import cn.nukkit.blockproperty.ArrayBlockProperty;

public enum StoneSlab2Type {
    RED_SANDSTONE,
    PURPUR,
    PRISMARINE_ROUGH,
    PRISMARINE_DARK,
    PRISMARINE_BRICK,
    MOSSY_COBBLESTONE,
    SMOOTH_SANDSTONE,
    RED_NETHER_BRICK;
    public static final ArrayBlockProperty<StoneSlab2Type> PROPERTY = new ArrayBlockProperty<>("stone_slab_type_2", values());
}
