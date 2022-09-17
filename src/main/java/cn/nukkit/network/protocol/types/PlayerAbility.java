package cn.nukkit.network.protocol.types;

import java.util.Arrays;
import java.util.List;

public enum PlayerAbility {
    BUILD,
    MINE,
    DOORS_AND_SWITCHES,
    OPEN_CONTAINERS,
    ATTACK_PLAYERS,
    ATTACK_MOBS,
    OPERATOR_COMMANDS,
    TELEPORT,
    INVULNERABLE,
    FLYING,
    MAY_FLY,
    INSTABUILD,
    LIGHTNING,
    FLY_SPEED,
    WALK_SPEED,
    MUTED,
    WORLD_BUILDER,
    NO_CLIP;

    public static final List<PlayerAbility> VALUES = Arrays.asList(values());
}
