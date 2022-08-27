package cn.nukkit.network.protocol.types;

public enum InputMode {

    UNDEFINED(0),
    MOUSE(1),
    TOUCH(2),
    GAME_PAD(3),
    MOTION_CONTROLLER(4),
    COUNT(5);

    private final int ordinal;

    private static final InputMode[] VALUES = values();

    InputMode(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public static InputMode fromOrdinal(int ordinal) {
        return VALUES[ordinal];
    }
}
