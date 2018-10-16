package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */
@AllArgsConstructor
public class Cactus implements Metadata {

    @Getter
    private final byte age;

    public static Cactus of(int age) {
        return new Cactus((byte) age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cactus)) return false;
        Cactus cactus = (Cactus) o;
        return age == cactus.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }

    @Override
    public String toString() {
        return "Cactus(" +
                "age=" + age +
                ')';
    }
}
