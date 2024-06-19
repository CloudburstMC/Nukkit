package cn.nukkit.network.protocol.types;

public enum ClientPlayMode {

    NORMAL,
    TEASER,
    SCREEN,
    VIEWER,
    REALITY,
    PLACEMENT,
    LIVING_ROOM,
    EXIT_LEVEL,
    EXIT_LEVEL_LIVING_ROOM;

    private static final ClientPlayMode[] VALUES = values();

    public static ClientPlayMode fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }
}
