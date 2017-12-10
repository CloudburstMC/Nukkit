package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Objects;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Crops implements Metadata {
    public static final Crops NEW = new Crops(0);
    public static final Crops FULLY_GROWN = new Crops(7);
    private final int level;

    public static Crops of(int data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "data is not valid (wanted 0-7)");
        return new Crops(data);
    }

    public int getLevel() {
        return level;
    }

    public boolean isFullyGrown() {
        return level == 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crops crops = (Crops) o;
        return level == crops.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level);
    }

    @Override
    public String toString() {
        return "Crops{" +
                "level=" + level +
                ", fullyGrown=" + isFullyGrown() +
                '}';
    }
}
