package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.data.SimpleDirection;

/**
 * @author CreeperFace
 */
public class EndPortalFrame extends SimpleDirectional {

    final boolean eye;

    EndPortalFrame(SimpleDirection direction, boolean eye) {
        super(direction);
        this.eye = eye;
    }

    public static EndPortalFrame of(SimpleDirection direction, boolean eye) {
        return new EndPortalFrame(direction, eye);
    }

    public boolean hasEye() {
        return eye;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDirection(), eye);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndPortalFrame that = (EndPortalFrame) o;
        return this.getDirection() == that.getDirection() && this.eye == that.eye;
    }

    @Override
    public String toString() {
        return "EndPortalFrame(" +
                "direction=" + getDirection() +
                "hasEye=" + eye +
                ')';
    }
}
