package cn.nukkit.server.network.minecraft.data.metadata;

public enum EntityMetadataData {

    FLAGS,
    HEALTH, // int
    VARIANT, // int
    COLOR, // byte
    NAMETAG, // String
    OWNER_EID, // long
    TARGET_EID, // long
    AIR, // short
    POTION_COLOR, // int
    POTION_AMBIENT, // byte
    UNKNOWN_10, // byte
    HURT_TIME, // int
    HURT_DIRECTION, // int
    PADDLE_TIME_LEFT, // float
    PADDLE_TIME_RIGHT, //float
    EXPERIENCE_VALUE, // int
    MINECART_DISPLAY_BLOCK, // int
    MINECART_DISPLAY_OFFSET, // int
    MINECART_HAS_DISPLAY, // byte
    UNKNOWN_19,
    UNKNOWN_20,
    UNKNOWN_21,
    UNKNOWN_22,
    ENDERMAN_HELD_ITEM_ID, // short
    ENDERMAN_HELD_ITEM_DAMAGE, // short
    ENTITY_AGE, // short
    UNKNOWN_26,
    UNKNOWN_27,
    UNKNOWN_28,
    UNKNOWN_29,
    FIREBALL_POWER_X, // float
    FIREBALL_POWER_Y, // float
    FIREBALL_POWER_Z, // float
    UNKNOWN_33,
    UNKNOWN_34,
    UNKNOWN_35,
    UNKNOWN_36,
    POTION_AUX_VALUE, // short
    LEAD_HOLDER_EID, // long
    SCALE, // float
    INTERACTIVE_TAG, // String
    NPC_SKIN_ID, // String
    URL_TAG, // String
    MAX_AIR, // short
    MAX_VARIANT, // int
    UNKNOWN_45,
    UNKNOWN_46,
    UNKNOWN_47,
    BLOCK_TARGET, // Vector3i
    WITHER_INVULNERABLE_TICKS, // int
    WITHER_TARGET_1, // long
    WITHER_TARGET_2, // long
    WITHER_TARGET_3, // long
    UNKNOWN_53, // short
    BOUDNING_BOX_WIDTH, // float
    BOUNDING_BOX_HEIGHT, // float
    FUSE_LENGTH, // int
    RIDER_SEAT_POSITION, // Vector3f
    RIDER_ROTATION_LOCKED, // byte
    RIDER_MAX_ROTATION, // float
    RIDER_MIN_ROTATION, // float
    AREA_EFFECT_CLOUD_RADIUS, // float
    AREA_EFFECT_CLOUD_WATING, // int
    AREA_EFFECT_CLOUD_PARTICLE_ID, // int
    UNKNOWN_64,
    SHULKER_ATTACH_FACE, // byte
    UNKNOWN_66,
    SHULKER_ATTACK_POS, // Vector3i
    TRADING_PLAYER_EID, // long
    UNKNOWN_69,
    UNKNOWN_70,
    COMMAND_BLOCK_COMMAND, // String
    COMMAND_BLOCK_LAST_OUTPUT, // String
    COMMAND_BLOCK_TRACK_OUTPUT, // byte
    CONTROLLING_RIDER_SEAT_NUMBER, // byte
    STRENGTH, // int
    MAX_STRENGTH, // int
    UNKNOWN_77, // int
    UNKNOWN_78; // int

    public enum Type {
        BYTE,
        SHORT,
        INT,
        FLOAT,
        STRING,
        SLOT,
        VECTOR3I,
        LONG,
        VECTOR3F
    }

    public enum Flag {
        ON_FIRE,
        SNEAKING,
        RIDING,
        SPRINTING,
        ACTION,
        INVISIBLE,
        TEMPTED,
        IN_LOVE,
        SADDLED,
        POWERED,
        IGNITED,
        BABY,
        CONVERTING,
        CRITICAL,
        CAN_SHOW_NAMETAG,
        ALWAYS_SHOW_NAMETAG,
        NO_AI,
        SILENT,
        WALLCLIMBING,
        CAN_CLIMB,
        SWIMMER,
        CAN_FLY,
        RESTING,
        SITTING,
        ANGRY,
        INTERESTED,
        CHARGED,
        TAMED,
        LEASHED,
        SHEARED,
        GLIDING,
        ELDER,
        MOVING,
        BREATHING,
        CHESTED,
        STACKABLE,
        SHOW_BASE,
        REARING,
        VIBRATING,
        IDLING,
        EVOKER_SPELL,
        CHARGE_ATTACK,
        WASD_CONTROLLED,
        CAN_POWER_JUMP,
        LINGER,
        HAS_COLLISION,
        AFFECTED_BY_GRAVITY,
        FIRE_IMMUNE,
        DANCING;

        public int id() {
            return ordinal();
        }
    }
}
