package cn.nukkit.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Helper class of Minecart variants
 * <p>
 * By Adam Matthew
 * Creation time: 2017/7/17 19:55.
 */
public enum MinecartType {
    /**
     * Represents an empty vehicle.
     */
    MINECART_EMPTY(0, false, "Minecart"),
    /**
     * Represents a chest holder.
     */
    MINECART_CHEST(1, true, "Minecart with Chest"),
    /**
     * Represents a furnace minecart.
     */
    MINECART_FURNACE(2, true, "Minecart with Furnace"),
    /**
     * Represents a TNT minecart.
     */
    MINECART_TNT(3, true, "Minecart with TNT"),
    /**
     * Represents a mob spawner minecart.
     */
    MINECART_MOB_SPAWNER(4, true, "Minecart with Mob Spawner"),
    /**
     * Represents a hopper minecart.
     */
    MINECART_HOPPER(5, true, "Minecart with Hopper"),
    /**
     * Represents a command block minecart.
     */
    MINECART_COMMAND_BLOCK(6, true, "Minecart with Command Block"),
    /**
     * Represents an unknown minecart.
     */
    MINECART_UNKNOWN(-1, false, "Unknown Minecart");

    private final int type;
    private final boolean hasBlockInside;
    private final String realName;
    private static final Int2ObjectMap<MinecartType> TYPES = new Int2ObjectOpenHashMap<>();

    static {
        for (MinecartType var3 : values()) {
            TYPES.put(var3.type, var3);
        }
    }

    MinecartType(int number, boolean hasBlockInside, String name) {
        type = number;
        this.hasBlockInside = hasBlockInside;
        realName = name;
    }

    /**
     * Get the variants of the current minecart
     *
     * @return Integer
     */
    public int getId() {
        return type;
    }

    /**
     * Get the name of the minecart variants
     *
     * @return String
     */
    public String getName() {
        return realName;
    }

    /**
     * Gets if the minecart contains block
     *
     * @return Boolean
     */
    public boolean hasBlockInside() {
        return hasBlockInside;
    }

    /**
     * Returns of an instance of Minecart-variants
     *
     * @param types The number of minecart
     * @return Integer
     */
    public static MinecartType valueOf(int types) {
        MinecartType what = TYPES.get(types);
        return what == null ? MINECART_UNKNOWN : what;
    }
}
