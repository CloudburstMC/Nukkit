package cn.nukkit.api.metadata.item;

import cn.nukkit.api.metadata.Metadata;

import java.util.Objects;

public final class GenericDamageValue implements Metadata {
    public static final GenericDamageValue ZERO = new GenericDamageValue((short) 0);
    private final short damage;

    public GenericDamageValue(short damage) {
        this.damage = damage;
    }

    public final short getDamage() {
        return damage;
    }

    @Override
    public final int hashCode() {
        return Objects.hash(damage);
    }

    @Override
    public final String toString() {
        return "GenericDamageValue(" +
                "damage=" + damage +
                ')';
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenericDamageValue that = (GenericDamageValue) o;
        return this.damage == that.damage;
    }
}
