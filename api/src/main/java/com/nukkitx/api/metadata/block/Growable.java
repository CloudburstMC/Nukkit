package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class Growable implements Metadata {

    @Getter
    final int age;

    public static Growable of(int age) {
        return new Growable(age);
    }

    public boolean isFullyGrown() {
        return age >= 7;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Growable that = (Growable) o;
        return this.age == that.age;
    }

    @Override
    public String toString() {
        return "Growable(" +
                "age=" + age +
                ')';
    }
}
