package com.nukkitx.server.inventory;

public enum WindowId {
    NONE(-1),
    INVENTORY(0),
    FIRST(1),
    LAST(100),
    OFFHAND(119),
    ARMOR(120),
    CREATIVE(121),
    HOTBAR(122),
    FIXED_INVENTORY(123),
    CURSOR(124);

    private final byte id;

    WindowId(int id) {
        this.id = (byte) id;
    }

    public byte id() {
        return id;
    }
}
