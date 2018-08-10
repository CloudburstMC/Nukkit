package com.nukkitx.api.metadata.block;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.data.SimpleDirection;

/**
 * @author CreeperFace
 */
public class Anvil extends SimpleDirectional {

    final int damage;

    Anvil(SimpleDirection direction, int damage) {
        super(direction);
        Preconditions.checkArgument(damage >= 0 && damage <= 2, "damage");
        this.damage = damage;
    }

    public Damage getDamage() {
        return Damage.values()[damage];
    }

    public static Anvil of(SimpleDirection direction, int damage) {
        return new Anvil(direction, damage);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDirection(), damage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Anvil that = (Anvil) o;
        return this.getDirection() == that.getDirection() && this.damage == that.damage;
    }

    @Override
    public String toString() {
        return "Anvil(" +
                "direction=" + getDirection() +
                "damage=" + damage +
                ')';
    }

    public enum Damage {
        NEW,
        SLIGHTLY,
        VERY
    }
}
