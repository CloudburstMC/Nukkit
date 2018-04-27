package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import cn.nukkit.api.metadata.data.SimpleDirection;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleDirectional implements Metadata {
    private final SimpleDirection direction;

    public static SimpleDirectional of(@Nonnull SimpleDirection direction) {
        Preconditions.checkNotNull(direction);
        return new SimpleDirectional(direction);
    }

    public SimpleDirection getDirection() {
        return direction;
    }

    @Override
    public int hashCode() {
        return direction.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleDirectional that = (SimpleDirectional) o;
        return this.direction == that.direction;
    }

    @Override
    public String toString() {
        return "SimpleDirectional(" +
                "direction=" + direction +
                ')';
    }
}
