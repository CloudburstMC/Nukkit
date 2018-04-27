package cn.nukkit.api.metadata.block;

import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Crops implements Metadata {
    public static final Crops NEW = new Crops((byte) 0);
    public static final Crops FULLY_GROWN = new Crops((byte) 7);

    private final byte stage;

    public static Crops of(int stage) {
        Preconditions.checkArgument(stage >= 0 && stage < 8, "stage is not valid (wanted 0-7)");
        return new Crops((byte) stage);
    }

    public byte getStage() {
        return stage;
    }

    public boolean isFullyGrown() {
        return stage == 7;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Crops that = (Crops) o;
        return this.stage == that.stage;
    }

    @Override
    public int hashCode() {
        return stage;
    }

    @Override
    public String toString() {
        return "Crops(" +
                "stage=" + stage +
                ", fullyGrown=" + isFullyGrown() +
                ')';
    }
}
