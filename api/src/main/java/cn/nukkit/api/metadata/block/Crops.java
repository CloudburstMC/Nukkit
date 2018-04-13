package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Crops implements Metadata {
    public static final Crops NEW = new Crops((byte) 0);
    public static final Crops FULLY_GROWN = new Crops((byte) 7);

    private final byte level;

    public static Crops of(int data) {
        Preconditions.checkArgument(data >= 0 && data < 8, "level is not valid (wanted 0-7)");
        return new Crops((byte) data);
    }

    public byte getLevel() {
        return level;
    }

    public boolean isFullyGrown() {
        return level == 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crops that = (Crops) o;
        return this.level == that.level;
    }

    @Override
    public int hashCode() {
        return 31 * level;
    }

    @Override
    public String toString() {
        return "Crops(" +
                "level=" + level +
                ", fullyGrown=" + isFullyGrown() +
                ')';
    }
}
