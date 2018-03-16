package cn.nukkit.api.permission;

public enum Ability {
    /**
     * World cannot be interacted with
     */
    WORLD_IMMUTABLE,
    /**
     * Player cannot hit other players.
     */
    NO_PVP,
    /**
     * Player cannot hit mobs.
     */
    NO_PVM,
    /**
     * Mobs cannot hit player
     */
    NO_MVP,
    /**
     * Can auto jump
     */
    AUTO_JUMP,
    /**
     * Is allowed to fly
     */
    ALLOWED_FLIGHT,
    /**
     * Can fly through the world
     */
    NO_CLIP,
    /**
     * Can edit the world
     */
    WORLD_BUILDER,
    /**
     * Is flying
     */
    FLYING,

    /**
     * Can place and destroy blocks
     */
    PLACE_AND_DESTROY,
    /**
     * Can interact with doors and switches
     */
    DOORS_AND_SWITCHES,
    /**
     * Can open containers e.g. Chests
     */
    OPEN_CONTAINERS,
    /**
     * Can attack other players
     */
    ATTACK_PLAYERS,
    /**
     * Can attack mobs
     */
    ATTACK_MOBS,
    /**
     * Is an operator
     */
    OPERATOR,
    /**
     * Can teleport
     */
    TELEPORT
}
