package cn.nukkit.entity;

public enum MountMode {
    RIDER(1),
    PASSENGER(2);

    private final byte id;

    MountMode(int id) {
        this.id = (byte) id;
    }

    public byte getId() {
        return id;
    }
}
