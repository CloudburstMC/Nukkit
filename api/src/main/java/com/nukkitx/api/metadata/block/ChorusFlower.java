package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE, staticName = "of")
public class ChorusFlower implements Metadata {

    @Getter
    final int age;

    public boolean isFullyGrown() {
        return age >= 5;
    }

    @Override
    public int hashCode() {
        return Objects.hash(age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChorusFlower that = (ChorusFlower) o;
        return this.age == that.age;
    }

    @Override
    public String toString() {
        return "ChorusFlower(" +
                "age=" + age +
                ')';
    }
}
