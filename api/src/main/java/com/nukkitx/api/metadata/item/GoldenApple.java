package com.nukkitx.api.metadata.item;

import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum GoldenApple implements Metadata {

    REGULAR,
    ENCHANTED;

    public final boolean isEnchanted() {
        return this == ENCHANTED;
    }

    @Override
    public String toString() {
        return "GoldenApple(" +
                "enchanted=" + isEnchanted() +
                ')';
    }
}
