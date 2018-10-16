package com.nukkitx.api.metadata.block;

import com.nukkitx.api.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author CreeperFace
 */
@AllArgsConstructor(staticName = "of")
public class DaylightSensor implements Metadata {

    @Getter
    private final byte level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DaylightSensor)) return false;
        DaylightSensor that = (DaylightSensor) o;
        return level == that.level;
    }

    @Override
    public int hashCode() {

        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return "DaylightSensor(" +
                "level=" + level +
                ')';
    }
}
