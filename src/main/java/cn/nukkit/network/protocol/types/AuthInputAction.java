package cn.nukkit.network.protocol.types;

public enum AuthInputAction {
    ASCEND,
    DESCEND,
    NORTH_JUMP,
    JUMP_DOWN,
    SPRINT_DOWN,
    CHANGE_HEIGHT,
    JUMPING,
    AUTO_JUMPING_IN_WATER,
    SNEAKING,
    SNEAK_DOWN,
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UP_LEFT,
    UP_RIGHT,
    WANT_UP,
    WANT_DOWN,
    WANT_DOWN_SLOW,
    WANT_UP_SLOW,
    SPRINTING,
    ASCEND_SCAFFOLDING,
    DESCEND_SCAFFOLDING,
    SNEAK_TOGGLE_DOWN,
    PERSIST_SNEAK,
    START_SPRINTING,
    STOP_SPRINTING,
    START_SNEAKING,
    STOP_SNEAKING,
    START_SWIMMING,
    STOP_SWIMMING,
    START_JUMPING,
    START_GLIDING,
    STOP_GLIDING,
    PERFORM_ITEM_INTERACTION,
    PERFORM_BLOCK_ACTIONS,
    PERFORM_ITEM_STACK_REQUEST,
    /**
     * @since v567
     */
    HANDLE_TELEPORT,
    /**
     * @since v575
     */
    EMOTING,
    /**
     * @since v594
     */
    MISSED_SWING,
    /**
     * @since v594
     */
    START_CRAWLING,
    /**
     * @since v594
     */
    STOP_CRAWLING,
    /**
     * @since v618
     */
    START_FLYING,
    /**
     * @since v618
     */
    STOP_FLYING,
    /**
     * @since v622
     */
    RECEIVED_SERVER_DATA,
    /**
     * @since v649
     */
    IN_CLIENT_PREDICTED_IN_VEHICLE,
    /**
     * @since v662
     */
    PADDLE_LEFT,
    /**
     * @since v662
     */
    PADDLE_RIGHT,
    /**
     * @since v685
     */
    BLOCK_BREAKING_DELAY_ENABLED;

    private static final AuthInputAction[] VALUES = values();

    public static AuthInputAction from(int id) {
        return VALUES[id];
    }

    public static int size() {
        return VALUES.length;
    }
}
