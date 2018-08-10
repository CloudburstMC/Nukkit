package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Cactus implements Metadata {

    @Getter
    private final int age;

    public static Cactus of(int age) {
        return new Cactus(age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cactus that = (Cactus) o;
        return this.age == that.age;
    }

    @Override
    public String toString() {
        return "Cactus(" +
                "age=" + age +
                ')';
    }
}
