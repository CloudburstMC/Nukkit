package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

/**
 * @author CreeperFace
 */
public class Cocoa extends SimpleDirectional {

    @Getter
    final Stage stage;

    Cocoa(SimpleDirection direction, Stage stage) {
        super(direction);
        this.stage = stage;
    }

    public static Cocoa of(SimpleDirection direction, Stage stage) {
        return new Cocoa(direction, stage);
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
