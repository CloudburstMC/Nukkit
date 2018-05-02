package com.nukkitx.api.util;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.Level;

import javax.annotation.concurrent.Immutable;

@Immutable
public final class Location {
    private final Vector3f position;
    private final Rotation rotation;
    private final Level level;

    public Location(Vector3f position, Level level) {
        this(position, level, Rotation.ZERO);
    }

    public Location(Vector3f position, Level level, Rotation rotation) {
        this.position = position;
        this.rotation = rotation;
        this.level = level;
    }

    public Level getLevel() {
        return level;
    }

    public Location setLevel(Level level) {
        return new Location(position, level, rotation);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Location setPosition(Vector3f position) {
        return new Location(position, level, rotation);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public Location setRotation(Rotation rotation) {
        return new Location(position, level, rotation);
    }
}
