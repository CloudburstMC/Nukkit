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
    PERFORM_ITEM_STACK_REQUEST;

    private static final AuthInputAction[] VALUES = values();

    public static AuthInputAction from(int id) {
        return VALUES[id];
    }

    public static int size() {
        return VALUES.length;
    }
}
