package cn.nukkit.entity.data;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum EntityData {
    FLAGS(0, 0),
    HEALTH(1), //int (minecart/boat)
    VARIANT(2), //int
    COLOR(3), //byte
    NAMETAG(4), //string
    OWNER_EID(5), //long
    TARGET_EID(6), //long
    AIR(7), //short
    POTION_COLOR(8), //int (ARGB!)
    POTION_AMBIENT(9), //byte
    JUMP_DURATION(10), //long
    HURT_TIME(11), //int (minecart/boat)
    HURT_DIRECTION(12), //int (minecart/boat)
    PADDLE_TIME_LEFT(13), //float
    PADDLE_TIME_RIGHT(14), //float
    EXPERIENCE_VALUE(15), //int (xp orb)
    DISPLAY_ITEM(16), //int (id | (data << 16))
    DISPLAY_OFFSET(17), //int
    HAS_DISPLAY(18), //byte (must be 1 for minecart to show block inside)
    //TODO: add more properties
    ENDERMAN_HELD_RUNTIME_ID(23), //short
    ENTITY_AGE(24), //short
    PLAYER_FLAGS(26), //byte
    /* 27 (int) player "index"? */
    PLAYER_BED_POSITION(28), //block coords
    FIREBALL_POWER_X(29), //float
    FIREBALL_POWER_Y(30),
    FIREBALL_POWER_Z(31),
    /* 32 (unknown)
     * 33 (float) fishing bobber
     * 34 (float) fishing bobber
     * 35 (float) fishing bobber */
    POTION_AUX_VALUE(36), //short
    LEAD_HOLDER_EID(37), //long
    SCALE(38), //float
    INTERACTIVE_TAG(39), //string (button text)
    NPC_SKIN_ID(40), //string
    URL_TAG(41), //string
    MAX_AIR(42), //short
    MARK_VARIANT(43), //int
    CONTAINER_TYPE(44), //byte
    CONTAINER_BASE_SIZE(45), //int
    CONTAINER_EXTRA_SLOTS_PER_STRENGTH(46), //int
    BLOCK_TARGET(47), //block coords (ender crystal)
    WITHER_INVULNERABLE_TICKS(48), //int
    WITHER_TARGET_1(49), //long
    WITHER_TARGET_2(50), //long
    WITHER_TARGET_3(51), //long
    /* 52 (short) */
    BOUNDING_BOX_WIDTH(53), //float
    BOUNDING_BOX_HEIGHT(54), //float
    FUSE_LENGTH(55), //int
    RIDER_SEAT_POSITION(56), //vector3f
    RIDER_ROTATION_LOCKED(57), //byte
    RIDER_MAX_ROTATION(58), //float
    RIDER_MIN_ROTATION(59), //float
    AREA_EFFECT_CLOUD_RADIUS(60), //float
    AREA_EFFECT_CLOUD_WAITING(61), //int
    AREA_EFFECT_CLOUD_PARTICLE_ID(62), //int
    /* 63 (int) shulker-related */
    SHULKER_ATTACH_FACE(64), //byte
    /* 65 (short) shulker-related */
    SHULKER_ATTACH_POS(66), //block coords
    TRADING_PLAYER_EID(67), //long

    /* 69 (byte) command-block */
    COMMAND_BLOCK_COMMAND(70), //string
    COMMAND_BLOCK_LAST_OUTPUT(71), //string
    COMMAND_BLOCK_TRACK_OUTPUT(72), //byte
    CONTROLLING_RIDER_SEAT_NUMBER(73), //byte
    STRENGTH(74), //int
    MAX_STRENGTH(75), //int
    // 76 (int)
    LIMITED_LIFE(77),
    ARMOR_STAND_POSE_INDEX(78), // int
    ENDER_CRYSTAL_TIME_OFFSET(79), // int
    ALWAYS_SHOW_NAMETAG(80), // byte
    COLOR_2(81), // byte
    // 82 unknown
    SCORE_TAG(83), //String
    BALLOON_ATTACHED_ENTITY(84), // long
    PUFFERFISH_SIZE(85),

    FLAGS_EXTENDED(91, 1),
    
    AREA_EFFECT_CLOUD_DURATION(94), // int
    AREA_EFFECT_CLOUD_SPAWN_TIME(95), // long
    AREA_EFFECT_CLOUD_RADIUS_PER_TICK(96), // float
    AREA_EFFECT_CLOUD_RADIUS_CHANGE_ON_PICKUP(97), // float
    AREA_EFFECT_CLOUD_PICKUP_COUNT(98), // int

    SKIN_ID(103);

    private static final Int2ObjectMap<EntityData> ID_MAP = new Int2ObjectOpenHashMap<>();

    static {
        for (EntityData type : values()) {
            ID_MAP.put(type.id, type);
        }
    }

    private final int id;
    private final int flagsIndex;

    EntityData(int id) {
        this(id, -1);
    }

    EntityData(int id, int flagsIndex) {
        this.id = id;
        this.flagsIndex = flagsIndex;
    }

    public int getId() {
        return id;
    }

    public static EntityData from(int id) {
        return ID_MAP.get(id);
    }

    public boolean isFlags() {
        return flagsIndex != -1;
    }

    public int getFlagsIndex() {
        return flagsIndex;
    }
}
