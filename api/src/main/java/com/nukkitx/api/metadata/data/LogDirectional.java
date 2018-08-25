package com.nukkitx.api.metadata.data;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class LogDirectional implements Metadata {

    @Getter
    private final LogDirection direction;

    public static LogDirectional of(@Nonnull LogDirection direction) {
        Preconditions.checkNotNull(direction);
        return new LogDirectional(direction);
    }

    @Override
    public int hashCode() {
        return direction.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogDirectional that = (LogDirectional) o;
        return this.direction == that.direction;
    }

    @Override
    public String toString() {
        return "LogDirectional(" +
                "direction=" + direction +
                ')';
    }
}
