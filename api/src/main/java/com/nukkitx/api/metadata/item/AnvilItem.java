package com.nukkitx.api.metadata.item;

import com.google.common.base.Objects;
import com.nukkitx.api.metadata.Anvil;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC, staticName = "of")
public class AnvilItem implements Anvil {

    @Getter
    final Damage damage;

    @Override
    public int hashCode() {
        return Objects.hashCode(damage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnvilItem that = (AnvilItem) o;
        return this.damage == that.damage;
    }

    @Override
    public String toString() {
        return "AnvilBlock(" +
                "damage=" + damage.name() +
                ')';
    }
}
