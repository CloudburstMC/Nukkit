package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;

/**
 * @author CreeperFace
 */
public class Cocoa extends SimpleDirectional {

    final int stage;

    Cocoa(SimpleDirection direction, int stage) {
        super(direction);
        Preconditions.checkArgument(stage >= 0 && stage <= 2, "stage");
        this.stage = stage;
    }

    public static Cocoa of(SimpleDirection direction, int stage) {
        return new Cocoa(direction, stage);
    }

    public Stage getStage() {
        return Stage.values()[stage];
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDirection(), stage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cocoa that = (Cocoa) o;
        return this.getDirection() == that.getDirection() && this.stage == that.stage;
    }

    @Override
    public String toString() {
        return "Cocoa(" +
                "direction=" + getDirection() +
                "stage=" + stage +
                ')';
    }

    public enum Stage {
        FIRST,
        SECOND,
        FINAL
    }
}
