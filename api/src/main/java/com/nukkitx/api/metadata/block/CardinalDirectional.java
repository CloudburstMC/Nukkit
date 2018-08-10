package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.metadata.data.CardinalDirection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class CardinalDirectional implements Metadata {
    private final CardinalDirection direction;

    public static CardinalDirectional of(@Nonnull CardinalDirection direction) {
        Preconditions.checkNotNull(direction, "direction");
        return new CardinalDirectional(direction);
    }

    public CardinalDirection getDirection() {
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
        CardinalDirectional that = (CardinalDirectional) o;
        return this.direction == that.direction;
    }

    @Override
    public String toString() {
        return "CardinalDirectional(" +
                "direction=" + direction +
                ')';
    }
}
