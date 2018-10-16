package com.nukkitx.api.metadata.block;

import com.google.common.base.Preconditions;
import com.nukkitx.api.metadata.Metadata;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TopSnow implements Metadata {

    @Getter
    private final byte layers;

    public static TopSnow of(int layers) {
        Preconditions.checkArgument(layers >= 0 && layers <= 7, "layers %s is not between 0 and 7", layers);
        return new TopSnow((byte) layers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopSnow topSnow = (TopSnow) o;
        return layers == topSnow.layers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers);
    }

    @Override
    public String toString() {
        return "TopSnow(" +
                "layers=" + layers +
                ')';
    }
}
