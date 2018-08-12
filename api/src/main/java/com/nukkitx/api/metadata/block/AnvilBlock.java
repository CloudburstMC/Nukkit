package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.Anvil;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.Getter;

/**
 * @author CreeperFace
 */
public class AnvilBlock extends SimpleDirectional implements Anvil {

    @Getter
    final Damage damage;

    AnvilBlock(SimpleDirection direction, Damage damage) {
        super(direction);
        this.damage = damage;
    }

    public static AnvilBlock of(SimpleDirection direction, Damage damage) {
        return new AnvilBlock(direction, damage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDirection(), damage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnvilBlock that = (AnvilBlock) o;
        return this.getDirection() == that.getDirection() && this.damage == that.damage;
    }

    @Override
    public String toString() {
        return "AnvilBlock(" +
                "direction=" + getDirection() +
                "damage=" + damage.name() +
                ')';
    }
}
