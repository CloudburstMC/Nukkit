package cn.nukkit.network.protocol.types;

public enum PlayerActionType {

    START_DESTROY_BLOCK,
    ABORT_DESTROY_BLOCK,
    STOP_DESTROY_BLOCK,
    GET_UPDATED_BLOCK,
    DROP_ITEM,
    START_SLEEPING,
    STOP_SLEEPING,
    RESPAWN,
    START_JUMP,
    START_SPRINTING,
    STOP_SPRINTING,
    START_SNEAKING,
    STOP_SNEAKING,
    CREATIVE_DESTROY_BLOCK,
    CHANGE_DIMENSION_ACK,
    START_GLIDING,
    STOP_GLIDING,
    DENY_DESTROY_BLOCK,
    CRACK_BLOCK,
    CHANGE_SKIN,
    UPDATED_ENCHANTING_SEED,
    START_SWIMMING,
    STOP_SWIMMING,
    START_SPIN_ATTACK,
    STOP_SPIN_ATTACK,
    INTERACT_WITH_BLOCK,
    PREDICT_DESTROY_BLOCK,
    CONTINUE_DESTROY_BLOCK,
    START_ITEM_USE_ON,
    STOP_ITEM_USE_ON,
    /**
     * @since v567
     */
    HANDLED_TELEPORT,
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
    STOP_FLYING;

    private static final PlayerActionType[] VALUES = values();

    public static PlayerActionType from(int id) {
        return VALUES[id];
    }

    public static PlayerActionType fromOrNull(int id) {
        if (id >= 0 && id < VALUES.length) {
            return VALUES[id];
        }
        return null;
    }
}
