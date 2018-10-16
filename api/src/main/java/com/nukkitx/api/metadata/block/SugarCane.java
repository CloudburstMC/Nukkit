package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */
@AllArgsConstructor
public class SugarCane implements Metadata {

    @Getter
    private final byte age;

    public static SugarCane of(int age) {
        return new SugarCane((byte) age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SugarCane)) return false;
        SugarCane sugarCane = (SugarCane) o;
        return age == sugarCane.age;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }

    @Override
    public String toString() {
        return "SugarCane(" +
                "age=" + age +
                ')';
    }
}
